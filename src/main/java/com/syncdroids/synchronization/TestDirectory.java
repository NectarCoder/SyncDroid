package com.syncdroids.synchronization;

import java.io.FileNotFoundException;

public class TestDirectory {
    public static void main(String[] args) throws FileNotFoundException {

        FileTree fn1 = new FileTree("C:\\Users\\amrutvyasa\\Desktop\\dir1\\");
        FileTree fn2 = new FileTree("C:\\Users\\amrutvyasa\\Documents\\dir1\\");

        System.out.println(fn1.equals(fn2));


    }

}
