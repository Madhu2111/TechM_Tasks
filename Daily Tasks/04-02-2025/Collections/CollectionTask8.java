
package com.collection.examples;

import java.util.ArrayList;

public class CollectionTask8 {
	public static void main(String args[]) {
		ArrayList<Integer> list=new ArrayList<>();
		list.add(1);
		list.add(92);
		list.add(32);
		list.add(44);
		list.add(99);
		list.add(85);
		list.add(30);
		System.out.println("original array:"+list);
		System.out.println("sorted array:");
		list.sort(null);
		System.out.println(list);
	}

}
