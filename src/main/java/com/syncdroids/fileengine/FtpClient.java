package com.syncdroids.fileengine;

import com.syncdroids.fileengine.exception.MissingCredentialsException;
import com.syncdroids.fileengine.exception.ServerUninitializedException;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
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
     * @param serverIp IP of the server.
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

    public void connect() throws ServerUninitializedException, MissingCredentialsException {
        if (serverip == null || port == null) {
            throw new ServerUninitializedException();
        }
        if (username == null || password == null) {
            throw new MissingCredentialsException();
        }
        try {
            open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        close();
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

        ftpClient.login(username, password);

    }

    private void close() throws IOException {
        ftpClient.disconnect();
    }




}
