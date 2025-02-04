package com.collection.examples;


import java.util.LinkedList;

public class CollectionTask12 {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();

        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        list.add("Date");
        list.add("Elderberry");

        System.out.println("Iterating through linked list using for-each loop:");
        usingForEach(list);

        System.out.println("\nIterating through linked list using iterator:");
        usingIterator(list);

        System.out.println("\nIterating through linked list using index:");
        usingIndex(list);
    }

    public static void usingForEach(LinkedList<String> list) {
        for (String element : list) {
            System.out.println(element + " ");
        }
        System.out.println();
    }

    public static void usingIterator(LinkedList<String> list) {
        list.iterator().forEachRemaining(element -> System.out.println(element + " "));
        System.out.println();
    }

    public static void usingIndex(LinkedList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i) + " ");
        }
        System.out.println();
    }
}
