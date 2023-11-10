package com.syncdroids.fileengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryParser {

    /**
     * Generates a FileTree object that contains the file hierarchy of a directory.
     *
     * @param directoryPath Path to the requested directory.
     * @return FileTree of the given directory path.
     * @throws FileNotFoundException If the path points to a nonexistent folder.
     * @throws NotDirectoryException If the path points to a file.
     */
    public static FileTree generateDirectoryTree(String directoryPath) throws FileNotFoundException, NotDirectoryException {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            throw new FileNotFoundException("Given directory: \"" + directoryPath + "\" does not exist or is invalid.");
        } else if (!directory.isDirectory()) {
            throw new NotDirectoryException("Given path: \"" + directoryPath + "\" does not indicate a valid directory (file found)");
        }

        FileTree root = new FileTree(directory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    root.getChildren().add(generateDirectoryTree(currentFile.getAbsolutePath()));
                } else {
                    root.getChildren().add(new FileTree(currentFile));
                }
            }
        }

        return root;

    }

    /**
     * Prints the FileTree contents as a readable list of file hierarchy.
     *
     */
    public static void printDirectoryTree(FileTree ft) {
        System.out.format("%" + "s%s\n", "", ft.getFile().getName());

        for (FileTree childFileTree : ft.getChildren()) {
            printDirectoryTree(childFileTree, 1);
        }
    }

    private static void printDirectoryTree(FileTree ft, int indentationLevel) {
        System.out.format("%" + indentationLevel + "s%s\n", "", ft.getFile().getName());

        for (FileTree childFileTree : ft.getChildren()) {
            printDirectoryTree(childFileTree, indentationLevel + 4);
        }
    }

}
