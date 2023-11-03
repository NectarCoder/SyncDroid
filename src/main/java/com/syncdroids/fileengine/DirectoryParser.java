package com.syncdroids.fileengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryParser {

    public static void generateDirectoryTree(String directoryPath) {

    }

    public static void displayAllFilesInDirectory(String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);
        Files.walk(directory)
                .forEach(path -> showFile(path.toFile()));
    }

    private static void showFile(File file) {
        if (file.isDirectory()) {
            System.out.println("Directory: " + file.getAbsolutePath());
        } else {
            System.out.println("File: " + file.getAbsolutePath());
        }
    }


}
