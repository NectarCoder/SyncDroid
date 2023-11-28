// HelloController.java
package com.syncdroids.ui;

import com.syncdroids.synchronization.FileNode;
import com.syncdroids.synchronization.FileTree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;


public class HelloController {

    private static final int USE_IMAGE_FOLDER = 0;
    private static final int USE_IMAGE_FILE = 1;

    @FXML
    public MenuItem exit;

    @FXML
    public MenuBar getPrimaryStage;

    @FXML
    public TreeView<String> localFolderTreeView;
    @FXML
    private TextField localFolderTextField;

    @FXML
    public TreeView<String> remoteFolderTreeView;
    public TextField remoteFolderTextField;

    @FXML
    private boolean isSetFTPServerDialogClosed = false;

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

            // Set a callback for when the dialog is closed
            dialog.setOnCloseRequest(event -> {
                isSetFTPServerDialogClosed = true;

                // You may perform additional actions if needed

                // Call initialize when the dialog is closed
                initialize();
            });

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }
    }

    @FXML
    protected void HelpViewHelp() {
        try {
            System.out.println("Launching View Help Dialog");

            // Load the FXML file for the FTP server dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("set-viewhelp.fxml"));

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
    protected void HelpAbout() {
        try {
            System.out.println("Launching About Dialog");

            // Load the FXML file for the FTP server dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("set-about.fxml"));

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

    private ImageView generateImageIcon(int type) {
        if (type == USE_IMAGE_FOLDER) {
            //Create an image for folders
            ImageView imageFolder = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/folder.png"))));
            imageFolder.setPreserveRatio(true);
            imageFolder.setFitWidth(16);
            return imageFolder;
        } else {
            //Create an image for files
            ImageView imageFile = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/file.png"))));
            imageFile.setPreserveRatio(true);
            imageFile.setFitWidth(16);
            return imageFile;
        }
    }

    @FXML
    protected void browseAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        // Show open file dialog
        File selectedDirectory = directoryChooser.showDialog(null);

        // If a folder is selected, update the text field and populate the tree view
        if (selectedDirectory != null) {
            localFolderTextField.setText(selectedDirectory.getAbsolutePath());
            try {
                FileTree selectedFileTree = new FileTree(selectedDirectory.getAbsolutePath());
                populateTreeView(selectedFileTree, localFolderTreeView);
            } catch (FileNotFoundException e) {
                e.printStackTrace(); // Handle the exception appropriately, e.g., show an error message
            }
        }
    }

    // Helper method to populate the TreeView with files and subdirectories
    private void populateTreeView(FileTree fileTree, TreeView<String> treeView) {
        treeView.setRoot(createNode(fileTree));
    }

    // Recursive method to create a TreeItem for a given directory
    private TreeItem<String> createNode(FileTree fileTree) {
        TreeItem<String> rootItem = new TreeItem<>(fileTree.getFileRoot().getFile().getName(), generateImageIcon(USE_IMAGE_FOLDER));
        rootItem.setExpanded(true);

        ArrayList<FileNode> children = (ArrayList<FileNode>) fileTree.getFileRoot().getChildren();

        for (int i = 0; i < fileTree.getFileRoot().getChildCount(); i++) {
            FileNode currentFileNode = children.get(i);
            File currentFile = currentFileNode.getFile();
            TreeItem<String> item = createNodeRecursive(currentFileNode);

            if (currentFile.isDirectory()) {
                item.setGraphic(generateImageIcon(USE_IMAGE_FOLDER));
            } else {
                item.setGraphic(generateImageIcon(USE_IMAGE_FILE));
            }

            rootItem.getChildren().add(item);
        }

        return rootItem;
    }

    private TreeItem<String> createNodeRecursive(FileNode fileNode) {
        TreeItem<String> item = new TreeItem<>(fileNode.getFile().getName());

        for (int i = 0; i < fileNode.getChildCount(); i++) {
            FileNode currentFileNode = fileNode.getChildAt(i);
            File currentFile = currentFileNode.getFile();
            TreeItem<String> childItem = createNodeRecursive(currentFileNode);

            if (currentFile.isDirectory()) {
                childItem.setGraphic(generateImageIcon(USE_IMAGE_FOLDER));
            } else {
                childItem.setGraphic(generateImageIcon(USE_IMAGE_FILE));
            }

            item.getChildren().add(childItem);
        }

        return item;
    }
    @FXML
    public void initialize() {
        if (isSetFTPServerDialogClosed) {
            try {
                // Read the server IP and port from data.txt
                String[] serverInfo = readServerInfoFromFile();

                // Set the server IP and port in the remoteFolderTextField
                if (serverInfo != null && serverInfo.length == 2) {
                    remoteFolderTextField.setText(serverInfo[0] + ":" + serverInfo[1]);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
            isSetFTPServerDialogClosed = false;
        }
    }
    // Helper method to read server IP and port from data.txt
    private String[] readServerInfoFromFile() throws IOException {
        File file = new File("data.txt");

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line contains server IP and port separated by ":"
                String[] serverInfo = line.split(":");
                if (serverInfo.length == 2) {
                    return serverInfo;
                }
            }
        }

        return null;
    }
}
