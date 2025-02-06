package com.exceptionproblems;

import java.util.Scanner;

public class NoCheck {
	public void noCheck(int n ) throws Exception2{
		if(n%2!=0) {
			throw new Exception2(n +"is an odd number");
		}
		else {
			System.out.println(n +"is even number");
		}
	}
	
	
	public static void main(String[] args){
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter a number:");
		int n=sc.nextInt();
		NoCheck obj=new NoCheck();
		try {
			obj.noCheck(n);
		}
		catch(Exception e){
			System.out.println("exception"+e);
		}
		
	}

}
