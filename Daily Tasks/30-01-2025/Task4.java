package com.examples;

import java.util.Arrays;

public class Task4 {

	public static void main(String ars[]) {
		
		int[] a1 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		int[] a2 = Arrays.copyOfRange(a1, 2, 8);
		
		for(int i=0;i<a2.length;i++) {
			System.out.print(a2[i]+" ");
		}
	}
}