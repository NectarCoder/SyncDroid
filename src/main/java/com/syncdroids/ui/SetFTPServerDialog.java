// SetFTPServerDialog.java
package com.syncdroids.ui;

import com.syncdroids.fileengine.FtpClient;
import com.syncdroids.fileengine.exception.MissingCredentialsException;
import com.syncdroids.fileengine.exception.ServerUninitializedException;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import javafx.scene.text.Font;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Scanner;

public class SetFTPServerDialog {

    @FXML
    private FtpClient ftpClient = new FtpClient();

    @FXML
    private void exitProgram() {
        // Disconnect from FTP server before exiting the program
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle disconnection exceptions appropriately
        }

        // Exit the program
        System.exit(0);
    }

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
    final HashMap<String, String> loginInfo = new HashMap<>();

    @FXML
    void changeVisibility(ActionEvent event) {
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
    void loginHandler(ActionEvent event) throws IOException {
        // Handle login attempt
        String username = usernameTextField.getText();
        String password = getPassword();
        updateUsernamesAndPasswords();

        String storedPassword = loginInfo.get(username);
        if (password.equals(storedPassword)) {
            System.out.println("Successfully login!");

            // Connect to FTP server after successful login
            ftpClient.setServerAddress(serverIpTextField.getText(), Integer.parseInt(portTextField.getText()));
            ftpClient.setCredentials(username, password);

            try {
                ftpClient.connect();
                // Perform FTP operations if needed
            } catch (ServerUninitializedException | MissingCredentialsException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }

            successField.setVisible(true);
        } else {
            errorField.setText("Wrong Password");
            errorField.setFont(new Font("Serif Bold", 12));
            errorField.setVisible(true);
        }
    }

    @FXML
    void createAccount(ActionEvent event) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
            String[] User = line.split(",",2);
            if (usernameTextField.getText().equals(User[0])) {
                System.out.println(User[0] + " already exists.");
                errorField.setFont(new Font("Serif Bold", 12));
                errorField.setText("User already exists");
                errorField.setVisible(true);
                return;
            }
                line = reader.readLine();
        }
        // Create a new user account
        writeToFile();

        // Connect to FTP server after creating an account
        ftpClient.setServerAddress(serverIpTextField.getText(), Integer.parseInt(portTextField.getText()));
        ftpClient.setCredentials(usernameTextField.getText(), getPassword());

        try {
            ftpClient.connect();
            // Perform FTP operations if needed
        } catch (ServerUninitializedException | MissingCredentialsException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }

        successField.setVisible(true);


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
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        System.out.println(username+password);
        writer.write(username + "," + password + "\n");
        writer.close();
    }
}
