package com.syncdroids.ui;

import com.syncdroids.fileengine.FtpClient;
import com.syncdroids.synchronization.*;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private FileTree localFileTree = null;
    private FtpFileTree remoteFileTree = null;

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
                this.currentFTPSession.setBufferSize(Integer.MAX_VALUE / 8);
                try {
                    this.currentFTPSession.setFileType(FtpClient.BINARY_TYPE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setRemoteFolderTextFieldServerInfo();
                try {
                    parseRemoteDirectory("/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startKeepAliveThread();
            } else {
                this.currentFTPSession = null;
            }
        });

        dialog.showAndWait();
    }

    public void parseRemoteDirectory(String directory) throws IOException {
        if (this.currentFTPSession != null && currentFTPSession.isServerCredentialsSet()) {
            FTPFile[] remoteFiles = currentFTPSession.getFTPClientSessionObject().listFiles(directory);
            FtpFileTree ftpFileRoot = new FtpFileTree(remoteFiles, currentFTPSession.getFTPClientSessionObject(), directory);
            this.remoteFileTree = ftpFileRoot;
        }
    }

    @FXML
    public void initOrRefreshRemoteTreeView() throws IOException {
        parseRemoteDirectory("/");

        //Do stuff to display remote folder contents in TreeView

    }

    @FXML
    public void synchronize() throws IOException {

        if (this.localFileTree == null) {
            // pop up msgbox saying error select local directory first
            return;
        } else if (this.remoteFileTree == null) {
            // pop up msgbox saying ensure connected to ftp server and directory is parsed
            return;
        }

        String localFileDirName = localFileTree.getFileRoot().getFile().getName();
        List<FtpFileNode> children = this.remoteFileTree.getChildren();
        boolean found = false;

        for (FtpFileNode child : children) {
            System.err.println(child.getFileOrDirName());
            if (child.isDirectory() && child.getFileOrDirName().equals(localFileDirName)) {
                found = true;
                break;
            }
        }

        if (!found) {
            currentFTPSession.getFTPClientSessionObject().makeDirectory(localFileDirName);
        }

        String syncPath = "/" + localFileDirName;

        System.err.println(syncPath);
        System.err.println(localFileDirName);

        compareEntriesAndTransfer(syncPath, this.localFileTree.getFileRoot());

    }

    private void compareEntriesAndTransfer(String remoteSyncPath, FileNode fileNode) throws IOException {
        System.err.println("INSIDE COMPARE METHOD");
        FTPClient session = this.currentFTPSession.getFTPClientSessionObject();

        FTPFileComparator comparator = new FTPFileComparator();
        FTPFile[] ftpFiles = session.listFiles(remoteSyncPath);
        Arrays.sort(ftpFiles, comparator);

        List<FileNode> localFiles = fileNode.getChildren();
        int i;
        boolean found = false;
        for (i = 0; i < localFiles.size(); i++) {
            FileNode currentFile = localFiles.get(i);
            for (FTPFile ftpFile : ftpFiles) {
                if (currentFile.getFile().getName().equals(ftpFile.getName())) {
                    found = true;
                    if (currentFile.isDirectory() && ftpFile.isDirectory()) {
                        compareEntriesAndTransfer(remoteSyncPath + "/" + currentFile.getFile().getName(), currentFile);
                    } else {
                        String remotePathName = remoteSyncPath + "/" + ftpFile.getName();
                        Instant localLastModifiedTime = Instant.ofEpochMilli(currentFile.getFile().lastModified());
                        Instant remoteLastModifiedTime = session.mdtmInstant(remotePathName);
                        if (localLastModifiedTime.isAfter(remoteLastModifiedTime)) {
                            // do transfer
                            try {
                                session.storeFile(remotePathName, new FileInputStream(currentFile.getFile()));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            System.err.println("MODIFIED TIME DO TRANSFER");
                        }
                    }
                    break;
                }
            }
            if (!found) {
                String remotePathName = remoteSyncPath + "/" + currentFile.getFile().getName();
                if (currentFile.isDirectory()) {
                    System.err.println("NOT FOUND IS DIR MAKE DIR");
                    //make dir here
                    session.makeDirectory(remotePathName);
                    compareEntriesAndTransfer(remotePathName, currentFile);
                } else {
                    // do transfer
                    System.err.println("NOT FOUND IS FILE DO TRANSFER");
                    try {
                        session.storeFile(remotePathName, new FileInputStream(currentFile.getFile()));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
        this.localFileTree = fileTree;
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

        //close keepAlive thread if applicable
        stopKeepAliveThread();

        // Exit the program
        System.exit(0);
    }

    private Thread keepAliveThread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!this.currentFTPSession.getFTPClientSessionObject().sendNoOp()) {
                    // Handle error
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(120000); //sleep 2 min
            } catch (InterruptedException e) {
                break;
            }
        }
    });

    private void startKeepAliveThread() {
        this.keepAliveThread.start();
    }

    private void pauseKeepAliveThread() {
        try {
            this.keepAliveThread.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resumeKeepAliveThread() {
        this.keepAliveThread.notify();
    }

    private void stopKeepAliveThread() {
        if (this.keepAliveThread != null) {
            this.keepAliveThread.interrupt();
        }
    }

}
