package com.collection.examples;

import java.util.LinkedList;
import java.util.Scanner;
public class CollectionTask15 {
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		System.out.println("enter the index");
		int index=sc.nextInt();
		System.out.println("Enter the element:");
		String ele=sc.next();
		LinkedList <String> list = new LinkedList <String> ();
		list.add("Apple");
		list.add("Orange");
		list.add("Berries");
		list.add("Grapes");
		list.add("Oranges");
		System.out.println("Original linked list: "+list);
		list.add(index-1, ele);
		System.out.println("Modified linked list:" + list);
	}
}