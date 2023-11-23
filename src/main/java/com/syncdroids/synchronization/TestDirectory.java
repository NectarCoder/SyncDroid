package com.syncdroids.synchronization;

import java.io.FileNotFoundException;

public class TestDirectory {
    public static void main(String[] args) throws FileNotFoundException {

        FileNode fn1 = new FileNode("C:\\Users\\amrutvyasa\\Desktop\\dir1\\");
        FileNode fn2 = new FileNode("C:\\Users\\amrutvyasa\\Documents\\dir1\\");

        System.out.println(fn1.equals(fn2));


    }

}
