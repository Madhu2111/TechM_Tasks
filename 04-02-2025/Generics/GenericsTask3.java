package com.generics.examples;

import java.util.List;

public class GenericsTask3 {
	public static <T> int firstOccur(List<T> list,T target) {
		if(list.size()==0) {
			return -1;
		}
		for(T num:list) {
			if(num == target) {
				return list.indexOf(num);
			}
			
			
		}
		return -1;
	}
	public static void main(String args[]){
		List<Integer> integerList = List.of(1, 2, 3, 4, 5, 6);
		System.out.println(firstOccur(integerList,1));
	}

}
