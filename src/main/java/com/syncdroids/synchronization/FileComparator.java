package com.syncdroids.synchronization;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        return file1.getName().compareTo(file2.getName());
    }
}
