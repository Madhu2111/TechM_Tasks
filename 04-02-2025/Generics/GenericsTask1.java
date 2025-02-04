package com.generics.examples;

public class GenericsTask1 {
    public static <T> boolean areArraysEqual(T[] array1, T[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (!array1[i].equals(array2[i])) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Integer[] array1 = {1, 2, 3, 4, 5};
        Integer[] array2 = {1, 2, 3, 4, 5};
       

        System.out.println("Are array1 and array2 equal: " + areArraysEqual(array1, array2));
        

        String[] stringArray1 = {"apple", "banana", "cherry"};
        String[] stringArray2 = {"apple", "banana", "cherry"};
        

        System.out.println("Are stringArray1 and stringArray2 equal: " + areArraysEqual(stringArray1, stringArray2));
        
    }
}
