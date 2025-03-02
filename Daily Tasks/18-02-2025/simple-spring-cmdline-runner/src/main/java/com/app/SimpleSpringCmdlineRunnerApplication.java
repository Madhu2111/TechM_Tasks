package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleSpringCmdlineRunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleSpringCmdlineRunnerApplication.class, args);
		System.out.println("It is a Starter Class");
	}

}
