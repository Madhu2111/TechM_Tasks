package com.exceptionproblems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Exception4 {
	static class PositiveNoException extends Exception{
		public PositiveNoException(String msg) {
			super(msg);
		}
	}
	public static void checkNo(String path) throws PositiveNoException{
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int number = Integer.parseInt(line.trim());
                    if (number > 0) {
                        throw new PositiveNoException("Positive number found: " + number);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format: " + line);
                }
            }
            System.out.println("All numbers are non-positive.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
	}

            public static void main(String[] args) {
                String path = "C:\\\\Users\\\\user\\\\eclipse-workspace\\\\myproject\\\\examplefile.txt"; 
                try {
                    checkNo(path);
                } catch (PositiveNoException e) {
                    System.out.println(e.getMessage());
                }
            }
}
