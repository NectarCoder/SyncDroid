package com.syncdroids.synchronization;

import java.io.FileNotFoundException;

public class FileTree {

    private FileNode root;

    public FileTree(String path) throws FileNotFoundException {
        this.root = new FileNode(path);
    }

    public FileNode getFileRoot() {
        return this.root;
    }

    public boolean equals(Object o) {
        // Input must be of type FileNode
        if (o.getClass() != FileTree.class) {
            return false;
        } else {
            FileTree other = (FileTree) o;
            return this.root.equals(other.getFileRoot());
        }
    }
}
