package com.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamsTask4 {
  public static void main(String[] args) {
    List < Integer > nums = Arrays.asList(20, 23, 22, 23, 24, 24, 33, 15, 36, 15, 36);
    System.out.println("Original List of numbers: " + nums);
    List < Integer > distinctNumbers = nums.stream()
      .distinct()
      .collect(Collectors.toList());
    System.out.println("After removing duplicates from the said list: " + distinctNumbers);
  }
}
