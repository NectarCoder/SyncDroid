package com.syncdroids.fileengine.deprecated;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class FileTree implements Comparable<FileTree> {
    private final File file;
    private final List<FileTree> children;

    public List<FileTree> getChildren() {
        return children;
    }

    public File getFile() {
        return file;
    }

    public FileTree(File file) {
        this.file = file;
        this.children = new ArrayList<>();
    }

    @Override
    public int compareTo(FileTree ft) {




        return 0;
    }
}
