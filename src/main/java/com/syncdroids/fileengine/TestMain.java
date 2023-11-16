package com.syncdroids.fileengine;

import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import static com.syncdroids.fileengine.DirectoryParser.*;

public class TestMain {

    public static void main(String[] args){
        FileTree ft = null;

        try {
            ft = generateDirectoryTree("C:\\Program Files\\Windows NT");
        } catch (FileNotFoundException | NotDirectoryException e) {
            e.printStackTrace();
        }

        if (ft != null) {
            printDirectoryTree(ft);
        }
    }

}
