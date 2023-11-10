package com.syncdroids.fileengine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTree {
    private final File file;
    private final List<FileTree> children;

    public List<FileTree> getChildren() {
        return children;
    }

    public FileTree(File file) {
        this.file = file;
        this.children = new ArrayList<>();
    }

    public File getFile() {
        return file;
    }


}
