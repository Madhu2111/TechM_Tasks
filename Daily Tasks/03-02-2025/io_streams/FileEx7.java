package com.io_streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileEx7 {
  public static void main(String[] args) throws IOException {
    BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Enter input: ");
    String str = read.readLine();
    System.out.println("Your input is: " + str);
  }
}