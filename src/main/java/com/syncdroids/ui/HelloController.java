// HelloController.java
package com.syncdroids.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import java.io.IOException;

public class HelloController {

    public MenuItem exit;

    @FXML
    protected void exitProgram() {
        // Exit the program
        System.exit(0);
    }

    @FXML
    protected void launchFTPDialog() {
        try {
            System.out.println("Launching FTP Dialog");

            // Load the FXML file for the FTP server dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("set-ftpserver.fxml"));

            // Create and show the dialog
            DialogPane dialogPane = loader.load();
            Dialog<Void> dialog = new Dialog<>();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setDialogPane(dialogPane);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }
    }
}
