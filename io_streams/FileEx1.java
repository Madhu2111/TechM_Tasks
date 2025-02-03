package com.files;

import java.io.File;

public class FileEx1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="C:\\Users\\user\\eclipse-workspace\\myproject";
		File directory=new File(path);
		if(directory.isDirectory()) {
			File[] contents=directory.listFiles();
			for(File file:contents) {
				System.out.println(file.getName());
			}
		}

	}

}
