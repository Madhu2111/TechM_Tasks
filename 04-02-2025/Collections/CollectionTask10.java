 package com.collection.examples;

import java.util.ArrayList;
import java.util.Collections;

public class CollectionTask10 {
	    public static void main(String[] args) {
	        ArrayList<String> list = new ArrayList<>();
	        list.add("Apple");
	        list.add("Banana");
	        list.add("Cherry");
	        list.add("Date");
	        list.add("Elderberry");

	        System.out.println("Original ArrayList:"+list);

	        Collections.shuffle(list);
	        System.out.println("\nShuffled ArrayList:");
	        printArrayList(list);
	    }

	    public static void printArrayList(ArrayList<String> list) {
	        for (String ele: list) {
	            System.out.print(ele+ " ");
	        }
	        System.out.println();
	    }
	}
