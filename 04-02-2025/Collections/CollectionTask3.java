package com.collection.examples;

import java.util.ArrayList;
import java.util.Scanner;

public class CollectionTask3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter a number to add at first position:");
		int n=sc.nextInt();
		ArrayList<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(0, n);
		System.out.print(list);

	}

}
