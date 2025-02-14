package com.io_streams;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileEx10 {
    public static void main(String a[]) {
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader("src\\file1.txt"));
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error : "+e.getMessage());
        } catch (IOException e) {
            System.out.println("Error : "+e.getMessage());
        }
    }
}