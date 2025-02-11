package com.streams;

import java.util.*;
import java.util.List;

public class StreamsTask7 {
    public static void main(String args[]) {
        List<Integer> list = Arrays.asList(10, 55, 3, 13, 39, 79, 51);
        System.out.println("List elements:" + list);
        
        Optional<Integer> max = list.stream().max(Integer::compareTo);
        System.out.println("Maximum Value: " + max.get());
        
        Optional<Integer> min = list.stream().min(Integer::compareTo);
        System.out.println("Minimum Value: " + min.get());
    }
}