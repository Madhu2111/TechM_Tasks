package com.collection.examples;

import java.util.ArrayList;

public class CollectionTask6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		System.out.println("Original array:"+list);
		list.remove(2);
		System.out.println(" Array after removing third element:"+list);

	}

}
