package com.syncdroids.synchronization;

import java.io.File;
import java.io.FileNotFoundException;

public class FileTree implements Comparable<FileTree> {

    private FileNode root;
    public FileTree(String path) throws FileNotFoundException {
        this.root = new FileNode(path);
    }

    @Override
    public int compareTo(FileTree o) {
        return 0;
    }



    /*
    public boolean parsePath(boolean forceParse) {
        if (subFilesAndFolders != null) {
            if (forceParse) {
                //re parse subFiles...
            } else {
                // don't do anything
            }

        } else {
        }
    }
     */


}
