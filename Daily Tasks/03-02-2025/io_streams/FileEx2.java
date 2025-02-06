package com.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileEx2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String Dpath="C:\\\\Users\\\\user\\\\eclipse-workspace\\\\myproject";
		String ReqExt=".txt";
		Path path=Paths.get(Dpath);
		if (Files.isDirectory(path)) {
            try (Stream<Path> walk = Files.walk(path, 1)) {
                walk.filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString().toLowerCase())
                    .filter(f -> f.endsWith(ReqExt))
                    .forEach(System.out::println);
            } catch (IOException e) {
                System.out.println("An error occurred while reading the directory: " + e.getMessage());
            }
        } else {
            System.out.println("The specified path is not a directory.");
        }

	}

}
