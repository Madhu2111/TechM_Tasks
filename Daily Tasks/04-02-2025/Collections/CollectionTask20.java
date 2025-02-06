package com.collection.examples;

import java.util.LinkedList;

public class CollectionTask20 {
	public static void main(String[] args) {
	     LinkedList<String> list = new LinkedList<String>();
	          list.add("Red");
	          list.add("Green");
	          list.add("Black");
	          list.add("Pink");
	          list.add("orange");
	          System.out.println("Original linked list:" + list);  
	           Object first_element = list.getFirst();
	           System.out.println("First Element is: "+first_element);
	           Object last_element = list.getLast();
	           System.out.println("Last Element is: "+last_element);
	        }
}
