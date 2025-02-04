package com.collection.examples;

import java.util.LinkedList;

/*public class CollectionTask14 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinkedList<Integer> list = new LinkedList<>();
		for(int i=1;i<15;i++) {
        list.add(i);
		}
		for (int i=list.size()-1; i>=0; i--) {
            System.out.println(list.get(i) + " ");
        }

	}

}*/

public class CollectionTask14 {
    public static void main(String[] args) {
    	LinkedList<Integer> list = new LinkedList<>();
		for(int i=0;i<15;i++) {
        list.add(i);
		}
        System.out.println("original list"+list);
       

        System.out.println("\nReversed LinkedList:");
        usingDescendingIterator(list);
    }

    

	public static void usingDescendingIterator(LinkedList<Integer> list) {
        LinkedList<Integer> reversedLinkedList = new LinkedList<>(list);
        while (!reversedLinkedList.isEmpty()) {
            System.out.print(reversedLinkedList.removeLast() + " ");
        }
    }

    
}
 