package com.examples;
import java.util.Arrays;

public class Task3 {
	
	public static void main(String args[]) {
		
		int[][] a1= {{1,2,3,4,5},{6,7,8}};
		int[][] a2=a1;
		int[][] a3=a1.clone();
		
		System.out.println(Arrays.deepToString(a1));
		System.out.println(Arrays.deepToString(a2));
		System.out.println(Arrays.deepToString(a3));
		
		System.out.println(a1.equals(a2));
		System.out.println(Arrays.deepEquals(a1, a2));
		
		System.out.println(a1.equals(a3));
		System.out.println(Arrays.deepEquals(a1, a3));

	}

}