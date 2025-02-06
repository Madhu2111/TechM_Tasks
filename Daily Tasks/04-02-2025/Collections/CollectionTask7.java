package com.collection.examples;

import java.util.ArrayList;
import java.util.Scanner;

public class CollectionTask7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter a number:");
		int n=sc.nextInt();
		ArrayList<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		try {
			System.out.println("Given element is at the index:"+list.indexOf(n));
		}
		catch(Exception e) {
			System.out.println(e);
		}
		

	}

}
