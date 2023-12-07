package com.syncdroids.ui;

import com.syncdroids.fileengine.FtpClient;
import com.syncdroids.synchronization.FileNode;
import com.syncdroids.synchronization.FileTree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class HelloController {

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

    protected FtpClient currentFTPSession = null;
    private SetFTPServerDialog setFTPServerDialog;

    private FileTree remoteFileTree = null;

    protected ImageView imageFolder = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/folder.png")), 16, 16, false, false));
    protected ImageView imageFile = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/file.png")), 16, 16, false, false));

    @FXML
    protected void launchFTPDialog() throws IOException {

        System.out.println("Launching FTP Dialog");

        // Load the FXML file for the FTP server dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("set-ftpserver.fxml"));

        // Create and show the dialog
        DialogPane dialogPane = loader.load();
        Dialog<Void> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Set FTP Server");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
        setFTPServerDialog = loader.getController();

        dialog.setOnShown(event -> setFTPServerDialog.serverInfoInDataFile());

        // Set a callback for when the dialog is closed
        dialog.setOnCloseRequest(event -> {
            FtpClient temp = this.setFTPServerDialog.getFtpClient();
            if (temp.isServerInfoSet() && temp.isServerCredentialsSet()) {
                this.currentFTPSession = temp;
                setRemoteFolderTextFieldServerInfo();
                try {
                    parseRemoteDirectory("/");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.currentFTPSession = null;
            }
        });

        dialog.showAndWait();
    }

    protected void parseRemoteDirectory(String directory) throws IOException {
        if(this.currentFTPSession != null && currentFTPSession.isServerCredentialsSet()){
            FTPFile[] remoteFiles = currentFTPSession.getFTPClientSessionObject().listFiles(directory);
            for (FTPFile file : remoteFiles) {
                if (file.isDirectory()) {
                    System.out.println("Directory: " + directory + "/" + file.getName());
                    parseRemoteDirectory(directory + "/" + file.getName());
                } else {
                    System.out.println("File: " + directory + "/" + file.getName());
                }
            }
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
        TreeItem<String> rootItem = new TreeItem<>(fileTree.getFileRoot().getFile().getName(), this.imageFolder);
        rootItem.setExpanded(true);

        ArrayList<FileNode> children = (ArrayList<FileNode>) fileTree.getFileRoot().getChildren();

        for (int i = 0; i < fileTree.getFileRoot().getChildCount(); i++) {
            FileNode currentFileNode = children.get(i);
            TreeItem<String> item = createNodeRecursive(currentFileNode);

            if (currentFileNode.isDirectory()) {
                ImageView childImage = new ImageView(this.imageFolder.getImage());
                item.setGraphic(childImage);
            } else {
                ImageView childImage = new ImageView(this.imageFile.getImage());
                item.setGraphic(childImage);
            }

            rootItem.getChildren().add(item);
        }

        return rootItem;
    }

    private TreeItem<String> createNodeRecursive(FileNode fileNode) {
        TreeItem<String> item = new TreeItem<>(fileNode.getFile().getName());

        for (int i = 0; i < fileNode.getChildCount(); i++) {
            FileNode currentFileNode = fileNode.getChildAt(i);
            TreeItem<String> childItem = createNodeRecursive(currentFileNode);

            if (currentFileNode.isDirectory()) {
                ImageView childImage = new ImageView(this.imageFolder.getImage());
                childItem.setGraphic(childImage);
            } else {
                ImageView childImage = new ImageView(this.imageFile.getImage());
                childItem.setGraphic(childImage);
            }

            item.getChildren().add(childItem);
        }

        return item;
    }

    /**
     * Set the data in the remote folder text field
     */
    @FXML
    public void setRemoteFolderTextFieldServerInfo() {
        try {
            // Read the server IP and port from data.txt
            String[] serverInfo = readServerInfoFromFile();

            // Set the server IP and port in the remoteFolderTextField
            if (serverInfo != null && serverInfo.length < 5) {
                remoteFolderTextField.setText(serverInfo[0] + ":" + serverInfo[1]);
            } else {
                remoteFolderTextField.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    /**
     * Helper method to read server IP and port from data.txt
     */
    private String[] readServerInfoFromFile() throws IOException {
        File file = new File("connection-data.txt");

        if (!file.exists()) {
            return null;
        } else {
            BufferedReader reader;
            String data;
            try {
                reader = new BufferedReader(new FileReader(file));
                data = reader.readLine();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return data.split(",");
        }
    }

    /**
     * Get an initialized FtpClient object to access its connections and session
     *
     * @param ftpClient The FTPClient object to be set
     */
    protected void setCurrentFTPSession(FtpClient ftpClient) {
        this.currentFTPSession = ftpClient;
    }

    /**
     * Display ViewHelp Dialog
     */
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
            dialog.setTitle("Help");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }
    }

    /**
     * Display About Dialog
     */
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
            dialog.setTitle("About");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
        }
    }

    @FXML
    protected void exitProgram() throws IOException {
        //close connections if any
        if (currentFTPSession != null) {
            currentFTPSession.disconnect();
        }

        // Exit the program
        System.exit(0);
    }
}
