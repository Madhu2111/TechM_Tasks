package com.collection.examples;

import java.util.LinkedList;

public class CollectionTask19 {

		 public static void main(String[] args) {

		  LinkedList <Integer> list = new LinkedList <> ();
		  for(int i=1;i<15;i++) {
		  list.add(i);
		  }
		  
		  System.out.println("Original linked list:" + list);
		  LinkedList <Integer> newlist = new LinkedList <> ();
		  newlist.add(100);
		  newlist.add(101);
		  list.addAll(1, newlist);

		  System.out.println("LinkedList:" + list);
		 }
}
