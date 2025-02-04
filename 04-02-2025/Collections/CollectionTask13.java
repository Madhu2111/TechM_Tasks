package com.collection.examples;

import java.util.LinkedList;
import java.util.Scanner;

public class CollectionTask13 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Entera position:");
		int n=sc.nextInt();
		LinkedList<Integer> list = new LinkedList<>();
		for(int i=0;i<15;i++) {
        list.add(i);
		}
		System.out.println("original list"+list);
		if(n<list.size()) {
			for (int i = n; i < list.size(); i++) {
	            System.out.println(list.get(i) + " ");
	        }
		}
		else {
			System.out.println("wrong index");
		}

	}

}
