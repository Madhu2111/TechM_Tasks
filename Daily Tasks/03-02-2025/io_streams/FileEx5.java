package com.io_streams;

import java.io.File;

public class FileEx5 {
  public static void main(String[] args) {
    File file_dir = new File("src\\file1.txt");
    
    if (file_dir.isDirectory()) {
      System.out.println(file_dir.getAbsolutePath() + " is a directory.");
    } else {
      System.out.println(file_dir.getAbsolutePath() + " is not a directory.");
    }
    
    if (file_dir.isFile()) {
      System.out.println(file_dir.getAbsolutePath() + " is a file.");
    } else {
      System.out.println(file_dir.getAbsolutePath() + " is not a file.");
    }
  }
}