// HelloController.java
package com.syncdroids.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import java.io.IOException;
import javafx.stage.DirectoryChooser;
import java.io.File;

public class HelloController {

    @FXML
    public MenuItem exit;

    @FXML
    public MenuBar getPrimaryStage;

    @FXML
    private TextField localFolderTextField;

    @FXML
    private TreeView<String> localFolderTreeView;

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

    @FXML
    protected void browseAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        // Show open file dialog
        File selectedDirectory = directoryChooser.showDialog(null);

        // If a folder is selected, update the text field
        if (selectedDirectory != null) {
            localFolderTextField.setText(selectedDirectory.getAbsolutePath());
            populateTreeView(selectedDirectory, localFolderTreeView);
        }
    }
    // Helper method to populate the TreeView with files and subdirectories
    private void populateTreeView(File directory, TreeView<String> treeView) {
        treeView.setRoot(createNode(directory));
    }

    // Recursive method to create a TreeItem for a given directory
    private TreeItem<String> createNode(File file) {
        TreeItem<String> root = new TreeItem<>(file.getName());
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                root.getChildren().add(createNode(subFile));
            } else {
                root.getChildren().add(new TreeItem<>(subFile.getName()));
            }
        }
        return root;
    }
}
