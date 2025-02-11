package com.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StreamsTask6 {
  public static void main(String[] args) {
    List < String > colors = Arrays.asList("Red", "Green", "Blue", "Pink", "Brown");
    System.out.println("Original List of strings(colors): " + colors);
    List < String > ascendingOrder = colors.stream()
      .sorted()
      .collect(Collectors.toList());
    List < String > descendingOrder = colors.stream()
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toList());

    System.out.println("\nSorted in Ascending Order: " + ascendingOrder);
    System.out.println("\nSorted in Descending Order: " + descendingOrder);
  }
}
