package com.collection.examples;

import java.util.LinkedList;

public class CollectionTask11{
	    public static void main(String[] args) {
	        LinkedList<String> list = new LinkedList<>();
	        list.add("Apple");
	        list.add("Banana");
	        list.add("Cherry");
	        list.add("Dates");
	        System.out.println("Linked List before appending:"+list);
	        list.addLast("Orange");

	        System.out.println("\nLinked List after appending:");
	        printLinkedList(list);
	    }

	    public static void printLinkedList(LinkedList<String> list) {
	        for (String element : list) {
	            System.out.print(element + " ");
	        }
	        System.out.println();
	    }
	}
