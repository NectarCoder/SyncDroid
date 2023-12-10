package com.syncdroids.synchronization;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpFileNode {

    /**
     * Holds a value of -1; used to indicate if the current node doesn't have children, whether it is a directory or not.
     */
    private static final int NO_CHILDREN = -1;
    private boolean isDirectory = false;
    private List<FtpFileNode> children = null;
    private int childCount;
    private final FTPFile currentFile;

    private String fullNixPath;

    public FtpFileNode(FTPFile file, FTPClient currentSession, String parentNixPath) throws IOException {
        this.currentFile = file;
        if(currentFile.isDirectory()){
            this.isDirectory = true;
            this.fullNixPath = parentNixPath + currentFile.getName() + "/";
            FTPFile[] tempListOfChildren = currentSession.listFiles(fullNixPath);
            this.childCount = tempListOfChildren.length;
            this.children = new ArrayList<>(childCount);
            for(FTPFile ftpFile : tempListOfChildren){
                this.children.add(new FtpFileNode(ftpFile, currentSession, fullNixPath));
            }
            System.out.println("DEBUG: Current directory name is: " + currentFile.getName());
        } else if (currentFile.isFile()) {
            this.childCount = NO_CHILDREN;
            this.isDirectory = false;
            System.out.println("DEBUG: Current file name is: " + currentFile.getName());
        }
    }

    public String getFullNixPath() {
        return fullNixPath;
    }

    public int getChildCount(){
        return this.childCount;
    }

    public FtpFileNode getChildAt(int i) {
        if (isDirectory && children != null && i >= 0 && i < children.size()) {
            return children.get(i);
        }
        return null;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getFileOrDirName(){
        return this.currentFile.getName();
    }
}
