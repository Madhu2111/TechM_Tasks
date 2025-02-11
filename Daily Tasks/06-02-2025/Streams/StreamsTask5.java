package com.streams;

import java.util.Arrays;
import java.util.List;

public class StreamsTask5 {
  public static void main(String[] args) {
    List < String > colors = Arrays.asList("Apple", "Guava", "Banana", "Papaya", "Grapes");
    System.out.println("Original list of strings (colors): " + colors);
    char startingLetter = 'B';
    long ctr = colors.stream()
      .filter(s -> s.startsWith(String.valueOf(startingLetter)))
      .count();
    System.out.println("\nNumber of colors starting with '" + startingLetter + "': " + ctr);
    char startingLetter1 = 'Y';
    ctr = colors.stream()
      .filter(s -> s.startsWith(String.valueOf(startingLetter1)))
      .count();
    System.out.println("\nNumber of colors starting with '" + startingLetter1 + "': " + ctr);
  }
}
