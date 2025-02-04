package com.generics.examples;

import java.util.ArrayList;
import java.util.List;

public class GenericsTask5 {
	public  <T> List mergeList(List<T> list1,List<T> list2) {
		List<T> list=new ArrayList();
		for(int i=0;i<list1.size()+list2.size();i++) {
			if(i<list1.size()) {
			list.add(list1.get(i));
			}
			if(i<list2.size()) {
			list.add(list2.get(i));
			}
		}
		
		
		
		
		return list;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenericsTask5 obj=new GenericsTask5();
		List<Integer> list1 = List.of(1, 2, 3, 4, 5, 6,7,8);
		List<Integer> list2 = List.of(1, 2, 3, 4, 5, 6);
		System.out.println("New list:"+obj.mergeList(list1,list2));

	}

}
