package com.syncdroids.fileengine;

import com.syncdroids.fileengine.exception.MissingCredentialsException;
import com.syncdroids.fileengine.exception.ServerUninitializedException;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.PrintWriter;

public class FtpClient {

    private String serverip;
    private String username;
    private String password;
    private Integer port;
    private FTPClient ftpClient;

    /**
     * Initializes a new FtpClient object.
     * Before using this class, set the server address and credentials with the setServerAddress() and setCredentials() methods.
     */
    public FtpClient() {
        this.serverip = null;
        this.username = null;
        this.password = null;
        this.port = null;
        this.ftpClient = null;
    }

    /**
     * Set server IP and port number.
     *
     * @param serverIp   IP of the server.
     * @param portNumber Server's IP port number.
     */
    public void setServerAddress(String serverIp, Integer portNumber) {
        this.serverip = serverIp;
        this.port = portNumber;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean connect() throws ServerUninitializedException {
        if ((serverip == null || port == null) || (serverip.isEmpty())) {
            throw new ServerUninitializedException();
        }

        try {
            open();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Login to the FTP server with class-set credentials
     *
     * @return True if authentication succeeded, False if not
     * @throws ServerUninitializedException If server info is not initialized
     * @throws MissingCredentialsException  If credentials are not initialized
     * @throws IOException                  Some IOException when sending request to server
     */
    public boolean login() throws ServerUninitializedException, MissingCredentialsException, IOException {
        if ((serverip == null || port == null) || (serverip.isEmpty())) {
            throw new ServerUninitializedException();
        }
        if ((username == null || password == null) || (username.isEmpty() || password.isEmpty())) {
            throw new MissingCredentialsException();
        }

        return ftpClient.login(this.username, this.password);

    }

    /**
     * Login to the FTP server with passed in credentials.
     *
     * @return True if authentication succeeded, False if not.
     * @throws ServerUninitializedException If server info is not initialized.
     * @throws IOException                  Some IOException when sending request to server.
     */
    public boolean login(String username, String password) throws ServerUninitializedException, IOException {
        if ((serverip == null || port == null) || (serverip.isEmpty())) {
            throw new ServerUninitializedException();
        }

        return ftpClient.login(username, password);

    }

    /**
     * Logout of the FTP server.
     *
     * @return True if logged out successfully, false if not.
     * @throws IOException Some IOException when sending request to server
     */
    public boolean logout() throws IOException {
        if(this.ftpClient != null){
            return this.ftpClient.logout();
        }
        return false;
    }

    /**
     * Disconnect from the FTP server
     *
     * @throws IOException Some IOException when sending request to server
     */
    public void disconnect() throws IOException {
        logout();
        if(this.ftpClient != null){
            ftpClient.disconnect();
        }
    }

    /**
     * Get the internal Apache FTPClient object wrapped by this class.
     *
     * @return The internal Apache FTPClient object.
     * @throws NullPointerException If the object is uninitialized and is null.
     */
    public FTPClient getFTPClientSessionObject() throws NullPointerException {
        if (this.ftpClient == null) {
            throw new NullPointerException();
        }
        return this.ftpClient;
    }

    public boolean isServerInfoSet() {
        if (this.serverip != null && !this.serverip.isEmpty() && this.port != null) {
            return true;
        }
        return false;
    }

    public boolean isServerCredentialsSet() {
        if (this.username != null && !this.username.isEmpty() && this.password != null && !this.password.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = (this.serverip != null) ? this.serverip : "serverip null ";
        result += (this.port != null) ? this.port : "port null ";
        result += (this.username != null) ? this.username : "username null ";
        result += (this.password != null) ? this.password : "password null ";
        return result;
    }

    private void open() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftpClient.connect(serverip, port);
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            throw new IOException("Exception connecting to FTP server: " + serverip + ":" + port);
        }
    }
}
