package com.generics.examples;

import java.util.List;

public class GenericsTask2 {
    public static <T extends Number> void calculateEvenOddSum(List<T> numbers) {
        Integer evenSum = 0;
        Integer oddSum = 0;

        for (T number : numbers) {
            Integer num = number.intValue();
            if (num % 2 == 0) {
                evenSum += num;
            } else {
                oddSum += num;
            }
        }

        System.out.println("Sum of even numbers: " + evenSum);
        System.out.println("Sum of odd numbers: " + oddSum);
    }

    public static void main(String[] args) {
        List<Integer> integerList = List.of(1, 2, 3, 4, 5, 6);
        

        System.out.println("Integer List:");
        calculateEvenOddSum(integerList);

    }
}

