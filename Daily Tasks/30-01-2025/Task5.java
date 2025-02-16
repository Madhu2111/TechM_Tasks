package com.examples;

import java.util.Scanner;
import java.lang.StringBuffer;

public class Task5 {
	
	public static void main(String args[]) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("enter the string");
		String s1 = sc.next();
		
		char[] s = s1.toCharArray();
		
		int i;
		for(i=0;i<s.length;i+=2) {
			s[i] = Character.toUpperCase(s[i]);
		}
		
		for(i=0;i<s.length;i++) {
			System.out.print(s[i]);
		}
		
		sc.close();
	}
}
