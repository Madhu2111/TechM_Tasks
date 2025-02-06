package com.collection.examples;

import java.util.LinkedList;
import java.util.Scanner;

public class CollectionTask18 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the  element:");
		String ele1=sc.next();
		LinkedList <String> list = new LinkedList <String> ();
		list.add("Apple");
		list.add("Orange");
		list.add("Berries");
		list.add("Grapes");
		list.add("Oranges");
		System.out.println("Original linked list: "+list);
		list.offerLast(ele1);
		System.out.println("Modified linked list:" + list);


	}
}
