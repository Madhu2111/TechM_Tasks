package com.io_streams;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

public class FileEx11 {

    public static void main(String a[]) {
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("src\\file1.txt"));
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
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
