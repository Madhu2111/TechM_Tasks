package com.collection.examples;

import java.util.LinkedList;
import java.util.Scanner;

public class CollectionTask16 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the 2 elements:");
		String ele1=sc.next();
		String ele2=sc.next();
		LinkedList <String> list = new LinkedList <String> ();
		list.add("Apple");
		list.add("Orange");
		list.add("Berries");
		list.add("Grapes");
		list.add("Oranges");
		System.out.println("Original linked list: "+list);
		list.addFirst(ele1);
		list.addLast(ele2);
		System.out.println("Modified linked list:" + list);

	}

}
