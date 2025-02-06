package com.exceptionproblems;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Exception3 {
	public static void readFile(String path) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
        	fis=new FileInputStream(path);
        	
        	System.out.println("File found");
        }
        catch(FileNotFoundException e) {
        	throw new FileNotFoundException("file is not present");
        	
        }
        finally {
        	if(fis!=null) {
        		try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}
	public static void main(String args[])  {
		String path = "C:\\Users\\user\\eclipse-workspace\\myproject\\examplefile";//File not found 
		//String path = "C:\\Users\\user\\eclipse-workspace\\myproject\\examplefile.txt";    output:File found
        try {
            readFile(path);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
		
	}

}
