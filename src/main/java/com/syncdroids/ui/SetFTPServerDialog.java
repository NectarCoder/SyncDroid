// SetFTPServerDialog.java
package com.syncdroids.ui;

import com.syncdroids.fileengine.FtpClient;
import com.syncdroids.fileengine.exception.MissingCredentialsException;
import com.syncdroids.fileengine.exception.ServerUninitializedException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class SetFTPServerDialog {

    @FXML
    public TextField passwordTextField;
    @FXML
    public TextField portTextField;
    @FXML
    public TextField serverIpTextField;
    @FXML
    public TextField usernameTextField;
    @FXML
    private CheckBox showPassword;
    @FXML
    private PasswordField hiddenPasswordTextField;
    @FXML
    public TextField errorField;
    @FXML
    public TextField successField;

    // File to store login information
    final File file = new File("data.txt");

    // HashMap to store login information
    public HashMap<String, String> loginInfo = new HashMap<>();

    @FXML
    private final FtpClient ftpClient = new FtpClient();

    @FXML
    void changeVisibility() {
        // Toggle visibility of password fields
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

    @FXML
    void loginHandler() throws IOException {
        // Handle login attempt
        String username = usernameTextField.getText();
        String password = getPassword();
        updateUsernamesAndPasswords();

        String storedPassword = loginInfo.get(username);
        if (password.equals(storedPassword)) {
            System.out.println("Successfully login!");

            // Perform FTP operations if needed
            ftpClient.setServerAddress(serverIpTextField.getText(), Integer.parseInt(portTextField.getText()));
            ftpClient.setCredentials(username, password);

            try {
                ftpClient.connect();
                // Connect to FTP server after successful login
            } catch (ServerUninitializedException | MissingCredentialsException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }

            writeToFile();

            successField.setText(serverIpTextField.getText() + ":" + portTextField.getText());
            successField.setVisible(true);
        } else {
            errorField.setText("Wrong Password");
            errorField.setFont(new Font("Serif Bold", 12));
            errorField.setVisible(true);
        }
    }

    private String getPassword() {
        // Get the visible or hidden password based on visibility
        if (showPassword.isSelected()) {
            return passwordTextField.getText();
        } else {
            System.out.println(hiddenPasswordTextField.getText());
            return hiddenPasswordTextField.getText();
        }
    }

    private void updateUsernamesAndPasswords() throws IOException {
        // Update the HashMap with login information from the file
        Scanner scanner = new Scanner(file);
        loginInfo.clear();

        while (scanner.hasNext()) {
            String[] splitInfo = scanner.nextLine().split(",");

            if (splitInfo.length >= 2) {
                loginInfo.put(splitInfo[0], splitInfo[1]);
            } else {
                System.err.println("Invalid data format in the file.");
            }
        }

        scanner.close();
    }

    private void writeToFile() throws IOException {
        // Write a new user account to the file
        String username = usernameTextField.getText();
        String password = getPassword();
        String serverIp = serverIpTextField.getText();
        String port = portTextField.getText();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

        // Write the username and password to the file
        writer.write(username + "," + password + "\n");

        // Write the server IP and port to the file
        writer.write(serverIp + ":" + port + "\n");
        writer.close();

    }
    @FXML
    private void initialize() {
        // Ensure the file exists or create it
        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write("dev,development\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle file creation error appropriately
            }
        }

    }
}
