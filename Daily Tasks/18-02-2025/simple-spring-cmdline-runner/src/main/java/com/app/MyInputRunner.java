package com.app;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyInputRunner implements CommandLineRunner {
	//it is an abstract method of a functional interface called as CommandLineRunner.
	//It is used to read the input at command line and display the output.
	public void run(String...args) throws Exception{
		System.out.println("Hi..Executing CommandLine Runner");
		System.out.println(args[0]);
		System.out.println(Arrays.asList(args));
		System.out.println("End of the Command Line Runner");
	}

}
