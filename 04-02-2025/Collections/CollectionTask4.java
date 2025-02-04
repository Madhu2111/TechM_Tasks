package com.collection.examples;

import java.util.ArrayList;
import java.util.Scanner;

public class CollectionTask4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter a index:");
		int n=sc.nextInt();
		ArrayList<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		if(n<list.size()) {
			System.out.print("The element at "+n+"th index is:"+list.get(n));
		}
		else {
			System.out.println("Index not found");
		}

sc.close();
	}

}
