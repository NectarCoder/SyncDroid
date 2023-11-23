package com.syncdroids.synchronization;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileNode {

    private boolean isDirectory;
    private List<FileNode> children = null;
    private int childCount;
    private final File currentFile;

    public FileNode(String path) throws InvalidPathException, NullPointerException, SecurityException, FileNotFoundException {

        //First check if path is valid or not!
        try {
            Paths.get(path);
        } catch (InvalidPathException ipe) {
            ipe.printStackTrace();
            throw new InvalidPathException(path, "Invalid path, illegal path received.");
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            throw new NullPointerException("Null pointer received, please enter a valid path.");
        }

        //Initialize path
        this.currentFile = new File(path);

        boolean exists = false;

        //Check to see if file/directory exists
        try {
            exists = this.currentFile.exists();
        } catch (SecurityException se) {
            se.printStackTrace();
            throw new SecurityException("File/folder access is denied by file system permissions.");
        }

        if (!exists) {
            throw new FileNotFoundException("Specified File or Directory does not exist");
        }

        this.isDirectory = this.currentFile.isDirectory();
        this.childCount = (this.isDirectory) ? (Objects.requireNonNull(this.currentFile.listFiles()).length) : -1;

        if (childCount != -1) {
            children = new ArrayList<>(childCount);
            populateChildren();
        }
    }

    /**
     * Populate all children of the directory represented byt his object
     * Returns false if this object represents a single file and not a directory
     *
     * @return true if all sub folders and files are recognized, false if this is not a directory
     * @throws FileNotFoundException If any children do not exist (very unlikely to happen unless another process is modifying same directory)
     */
    public boolean populateChildren() throws FileNotFoundException {
        if (this.isDirectory) {

            File[] childrenFiles = this.currentFile.listFiles();
            for (File child : childrenFiles) {
                children.add(new FileNode(child.getPath()));
            }
            Arrays.sort(childrenFiles);

            return true;
        }

        return false;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public List<FileNode> getChildren() {
        return this.children;
    }

    public File getFile() {
        return this.currentFile;
    }

    public int getChildCount() {
        return this.childCount;
    }

    @Override
    public boolean equals(Object o) {
        // Input must be of type FileNode
        if (o.getClass() != FileNode.class) {
            return false;
        }

        //Cast input to a FileNode object
        FileNode other = (FileNode) o;

        // Check if both are directories
        if (this.isDirectory && other.isDirectory()) {
            // Check the number of children, if not equal, false
            if (this.childCount != other.getChildCount()) {
                return false;
            } else if (!this.currentFile.getName().equals(other.getFile().getName())) { // Directory names should be same
                return false;
            } else { // Compare every child with each other
                for (int i = 0; i < this.childCount; i++) {
                    if (!this.children.get(i).equals(other.children.get(i))) {
                        return false;
                    }
                }
            }
            // If both are NOT directories, i.e. they are files
        } else if (!(this.isDirectory) && !(other.isDirectory())) {
            if (!this.currentFile.getName().equals(other.getFile().getName())) {
                return false;
            }
            try {
                return FileUtils.contentEqualsIgnoreEOL(this.currentFile, other.getFile(), null);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Return false if one is a directory and another is a file (not comparable!)
            return false;
        }

        return true;
    }

    /*
    @Override
    public int compareTo(FileNode other) {

        //Check if both are directories
        if (this.isDirectory && other.isDirectory) {

            //Check which directory has more children in it
            if (this.childCount > other.childCount) {
                return 1;
            } else if (this.childCount < other.childCount) {
                return -1;
            } else {

            }
        } else { // Either one or both are files

            //See if current or the other or neither are directories
            if (this.isDirectory) {
                return 1;
            } else if (other.isDirectory) {
                return -1;
            } else {

                //Compare file sizes
                if (this.currentFile.getTotalSpace() > other.currentFile.getTotalSpace()) {
                    return 1;
                } else if (this.currentFile.getTotalSpace() < other.currentFile.getTotalSpace()) {
                    return -1;
                } else {
                    boolean dataEquals = false;
                    try {
                        dataEquals = FileUtils.contentEquals(this.currentFile, other.getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (dataEquals){
                        return 0;
                    }
                    else {
                        return 1;
                    }

                }
            }
        }


        return 0;
    }
     */
}
