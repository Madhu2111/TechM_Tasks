package com.examples;

public class Task1 {
	
	public static void main(String args[]) {
	
		int a1[] = {10,20,30,40,50,60,70,80,90,100};
		int a2[] = {1,2,3,4,5,6,7,8,9};
		
		int i;
		
		for(i=0;i<a1.length;i+=2) {
			System.arraycopy(a1, i, a2, i, 1);
		} 
		
		for(i=0;i<a1.length;i++) {
			System.out.print(a2[i]+" ");
		}
	
	}

}