// SetFTPServerDialog.java
package com.syncdroids.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SetFTPServerDialog {

    @FXML
    private void exitProgram() {
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
    File file = new File("data.txt");

    // HashMap to store login information
    HashMap<String, String> loginInfo = new HashMap<>();

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
            successField.setVisible(true);
        } else {
            errorField.setVisible(true);
        }
    }

    @FXML
    void createAccount(ActionEvent event) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Create a new user account
        writeToFile();
        successField.setVisible(true);
    }

    private String getPassword() {
        // Get the visible or hidden password based on visibility
        if (passwordTextField.isVisible()) {
            return passwordTextField.getText();
        } else {
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

    private void writeToFile() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // Write a new user account to the file
        String username = usernameTextField.getText();
        String password = getPassword();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        writer.write(username + "," + password + "\n");
        writer.close();
    }
}
