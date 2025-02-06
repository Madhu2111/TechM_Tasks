package com.generics.examples;

import java.util.ArrayList;
import java.util.List;

public class GenericsTask4 {
	public static <T> List reverseList(List<T> list) {
		List<T> revList = new ArrayList() ;
		for(int i=list.size()-1;i>=0;i--) {
			revList.add(list.get(i));
		}
		return revList;
	} 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> integerList = List.of(1, 2, 3, 4, 5, 6);
		System.out.println(reverseList(integerList));
	}

}
