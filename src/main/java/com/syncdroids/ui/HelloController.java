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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.stage.DirectoryChooser;
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
    public TreeView localFolderTreeView;
    @FXML
    private TextField localFolderTextField;

    @FXML
    public TreeView remoteFolderTreeView;
    public TextField remoteFolderTextField;

    @FXML
    protected void exitProgram() {
        // Exit the program
        System.exit(0);
    }

    @FXML
    protected void launchFTPDialog() throws FileNotFoundException {
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
    protected void HelpViewHelp() throws FileNotFoundException {
        try {
            System.out.println("Launching FTP Dialog");

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
    protected void HelpAbout() throws FileNotFoundException {
        try {
            System.out.println("Launching FTP Dialog");

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

    @FXML
    protected void initializeLocalTreeView() throws FileNotFoundException {
        FileTree localFT = new FileTree("C:\\Users\\amrutvyasa\\Desktop\\dir1\\");
        ArrayList<FileNode> children = (ArrayList<FileNode>) localFT.getFileRoot().getChildren();

        TreeItem<String> rootItem = new TreeItem<>(localFT.getFileRoot().getFile().getName(), generateImageIcon(USE_IMAGE_FOLDER));
        rootItem.setExpanded(true);

        for (int i = 0; i < localFT.getFileRoot().getChildCount(); i++) {
            File currentFile = children.get(i).getFile();
            TreeItem<String> item = new TreeItem<>(currentFile.getName());

            if (currentFile.isDirectory()) {
                item.setGraphic(generateImageIcon(USE_IMAGE_FOLDER));
            } else {
                item.setGraphic(generateImageIcon(USE_IMAGE_FILE));
            }
            rootItem.getChildren().add(item);
        }

        localFolderTreeView.setRoot(rootItem);
    }

    private ImageView generateImageIcon(int type){
        if (type == USE_IMAGE_FOLDER){
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
