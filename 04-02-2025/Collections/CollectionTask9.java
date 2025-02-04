package com.collection.examples;

import java.util.ArrayList;

public class CollectionTask9 {
    public static void main(String[] args) {
        ArrayList<String> originalList = new ArrayList<>();
        originalList.add("Apple");
        originalList.add("Banana");
        originalList.add("Cherry");
        originalList.add("Date");
        System.out.println("Original ArrayList:");
        System.out.println(originalList);
        ArrayList<String> copiedList = new ArrayList<>();
        copiedList=originalList;
        //ArrayList<String> copiedList = new ArrayList<>(originalList);  method 2
        System.out.println("\nCopied ArrayList:");
        System.out.println(copiedList);
    }
}
