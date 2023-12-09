package com.syncdroids.synchronization;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpFileTree {

    /**
     * Holds a value of -1; used to indicate if the current node doesn't have children, whether it is a directory or not.
     */
    private static final int NO_CHILDREN = -1;
    private boolean isDirectory;
    private List<FtpFileNode> children = null;
    private int childCount;
    private String serverNixPath = null;

    public FtpFileTree(FTPFile[] ftpFileChildren, FTPClient currentSession, String rawPath) throws IOException {
        this.serverNixPath = rawPath;
        this.isDirectory = true;

        if (ftpFileChildren != null || ftpFileChildren.length != 0) {
            this.childCount = ftpFileChildren.length;
            this.children = new ArrayList<>(childCount);
            for (FTPFile file : ftpFileChildren) {
                this.children.add(new FtpFileNode(file, currentSession, serverNixPath));
            }
        } else {
            this.childCount = -1;
        }

    }

    public String getServerNixPath(){
        return this.serverNixPath;
    }

    public String getFtpFileRootName(){
        String[] pathSplit = this.serverNixPath.split("/");
        return pathSplit[pathSplit.length-1];
    }

    public List<FtpFileNode> getChildren() {
        return children;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
