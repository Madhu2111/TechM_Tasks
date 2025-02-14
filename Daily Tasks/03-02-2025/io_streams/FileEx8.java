package com.io_streams;

import java.io.File;

public class FileEx8 {

    public static void main(String[] args) {
        File file = new File("src\\");
        if (file.exists()) {
        	long fileSize = file.length();
            System.out.println((double) fileSize + " Bytes");
            System.out.println((double) fileSize / 1024 + "  KB");
            System.out.println((double) fileSize / (1024 * 1024) + " MB");
        } else
            System.out.println("File does not exist");
    }

}
