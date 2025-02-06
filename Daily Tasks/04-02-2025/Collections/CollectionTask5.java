package com.collection.examples;

import java.util.Scanner;

public class CollectionTask5 {
    public static void main(String[] args) {
        // Initialize an array
        int[] array = {1, 2, 3, 4, 5};

        // Print the original array
        System.out.println("Original array:");
        printArray(array);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the index of the element to update:");
        int index = scanner.nextInt();
        System.out.println("Enter the new value:");
        int newValue = scanner.nextInt();
        updateArrayElement(array, index, newValue);

        System.out.println("\nUpdated array:");
        printArray(array);

    }

    public static void updateArrayElement(int[] array, int index, int newValue) {
        if (index >= 0 && index < array.length) {
            array[index] = newValue;
        } else {
            System.out.println("Invalid index. Index should be between 0 and " + (array.length - 1));
        }
    }

    public static void printArray(int[] array) {
        for (int element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
}
