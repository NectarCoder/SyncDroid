// SetFTPServerDialog.java
package com.syncdroids.ui;

import com.syncdroids.fileengine.FtpClient;
import com.syncdroids.fileengine.ServerUninitializedException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;

public class SetFTPServerDialog {

    //SERVER CONNECTION FIELDS
    @FXML
    public TextField serverIpTextField;
    @FXML
    public TextField portTextField;

    //SERVER AUTHENTICATION FIELDS
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField passwordTextField;

    //PASSWORD SECURITY FIELDS
    @FXML
    private CheckBox showPassword;
    @FXML
    private PasswordField hiddenPasswordTextField;

    //SUCCESS OR ERROR FIELDS
    @FXML
    public TextField successField;
    @FXML
    public TextField errorField;

    // File to store login information
    final File file = new File("connection-data.txt");

    // FTP client object for establishing connection with server
    @FXML
    private FtpClient ftpClient = new FtpClient();

    @FXML
    void loginHandler() throws IOException {

        //Get all the data in the fields
        String serverIP = serverIpTextField.getText();
        String serverPort = portTextField.getText();
        String username = usernameTextField.getText();
        String password = getPassword();

        //Validate all the fields
        if (serverIP == null || serverIP.isEmpty()) {
            setErrorField("Server IP not provided!");
        } else if (serverPort == null || serverPort.isEmpty()) {
            setErrorField("Server port number not provided!");
        } else if (username == null || username.isEmpty()) {
            setErrorField("Username credential not provided!");
        } else if (password == null || password.isEmpty()) {
            setErrorField("Password credential not provided!");
        } else { //Now try to connect to server with given data

            this.ftpClient.setServerAddress(serverIP, Integer.parseInt(serverPort));
            ftpClient.setCredentials(username, password);

            boolean isConnectSuccessful;
            boolean isLoginSuccessful;

            try {
                isConnectSuccessful = ftpClient.connect();
            } catch (ServerUninitializedException e) {
                e.printStackTrace();
                setErrorField("Server not initialized.");
                resetFtpClient();
                return;
            }

            if (!isConnectSuccessful) {
                setErrorField("Error: Server not found. Please try again.");
                resetFtpClient();
                return;
            } else {
                try {
                    isLoginSuccessful = ftpClient.login(username, password);
                } catch (ServerUninitializedException e) {
                    e.printStackTrace();
                    setErrorField("Server not initialized.");
                    resetFtpClient();
                    return;
                }
            }

            if (!isLoginSuccessful) {
                setErrorField("Error: Login credentials invalid. Please try again.");
                resetFtpClient();
            } else {
                /* If the connection is successful:
                 * - set success message for user
                 * - write the info to file for later connecting
                 */
                setSuccessField("Successfully connected to the FTP server.");
                writeToFile(new String[]{serverIP, serverPort, username, password});
            }
        }
    }

    protected boolean serverInfoInDataFile() {
        if(this.file.exists()){
            BufferedReader reader;
            String data;
            try {
                reader = new BufferedReader(new FileReader(file));
                data = reader.readLine();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            String[] serverInfo = data.split(",");
            this.serverIpTextField.setText(serverInfo[0]);
            this.portTextField.setText(serverInfo[1]);
            this.usernameTextField.setText(serverInfo[2]);
            this.passwordTextField.setText(serverInfo[3]);
            this.hiddenPasswordTextField.setText(serverInfo[3]);

            this.ftpClient.setServerAddress(serverInfo[0], Integer.parseInt(serverInfo[1]));
            this.ftpClient.setCredentials(serverInfo[2], serverInfo[3]);

            return true;
        }
        return false;
    }

    protected FtpClient getFtpClient() {
        return this.ftpClient;
    }

    private void writeToFile(String[] info) throws IOException {
        //Check if connection-data.txt file exists, if not create file.
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

        /*
         * Write the server IP, port, username, and password to the file (using , as delimiter)
         * index 0 is IP, 1 is port, 2 is username, 3 is password
         */
        writer.write(info[0] + "," + info[1] + "," + info[2] + "," + info[3]);
        writer.close();
    }

    // Get the visible or hidden password based on visibility
    private String getPassword() {
        if (showPassword.isSelected()) {
            return passwordTextField.getText();
        } else {
            return hiddenPasswordTextField.getText();
        }
    }

    private void setErrorField(String errorMessage) {
        successField.setVisible(false);
        errorField.setVisible(false);
        errorField.setText(errorMessage);
        errorField.setVisible(true);
    }

    private void setSuccessField(String successMessage) {
        errorField.setVisible(false);
        successField.setVisible(false);
        successField.setText(successMessage);
        successField.setVisible(true);
    }

    // Toggle visibility of password fields
    @FXML
    private void changeVisibility() {
        if (showPassword.isSelected()) {
            passwordTextField.setText(hiddenPasswordTextField.getText());
            passwordTextField.setVisible(true);
            hiddenPasswordTextField.setVisible(false);
            return;
        }
        hiddenPasswordTextField.setText(passwordTextField.getText());
        hiddenPasswordTextField.setVisible(true);
        passwordTextField.setVisible(false);
    }

    private void resetFtpClient(){
        this.ftpClient = new FtpClient();
    }
}
