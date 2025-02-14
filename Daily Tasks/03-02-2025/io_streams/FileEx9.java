package com.io_streams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileEx9 {
    public static void main(String a[]) {
        String file = "src\\file1.txt";
        InputStream fins = null;
        try {
            fins = new FileInputStream(file);
            byte file_content[] = new byte[2 * 1024];
            int read_count = 0;
            while ((read_count = fins.read(file_content)) > 0) {
                System.out.println(new String(file_content, 0, read_count - 1));
            }
        } catch (FileNotFoundException e) {
        	System.out.println("Error: "+e.getMessage());
        } catch (IOException e) {
        	System.out.println("Error: "+e.getMessage());
        } finally {
            try {
                if (fins != null)
                    fins.close();
            } catch (Exception e) {
            	System.out.println("Error: "+e.getMessage());
            }
        }
    }
}
