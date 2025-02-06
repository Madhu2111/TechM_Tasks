package com.collection.examples;

import java.util.ArrayList;
import java.util.Iterator;

public class CollectionTask2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> color=new ArrayList<>();
		color.add("orange");
		color.add("yellow");
		color.add("blue");
		color.add("black");
		Iterator<String> i= color.iterator();
		
		while(i.hasNext()) {
			String colors=i.next();
			System.out.println(colors);
		}

	}

}
