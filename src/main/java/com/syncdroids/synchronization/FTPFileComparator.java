package com.syncdroids.synchronization;

import org.apache.commons.net.ftp.FTPFile;

import java.util.Comparator;

public class FTPFileComparator implements Comparator<FTPFile>{
    @Override
    public int compare(FTPFile file1, FTPFile file2){
        return file1.getName().compareTo(file2.getName());
    }
}
