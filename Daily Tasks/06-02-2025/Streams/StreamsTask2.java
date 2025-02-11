package com.streams;

	import java.util.List;
	import java.util.Arrays;
	import java.util.stream.Collectors;

	public class StreamsTask2 {
		public static void main(String[] args) {
			List<String> names = Arrays.asList("Madhu", "sriram", "Dinesh", "Vamshi", "Siddu");
			System.out.println("List elements: " + names);
			
			List<String> uppercase = names.stream().map(String::toUpperCase).collect(Collectors.toList());
			System.out.println("Uppercase Strings: " + uppercase);
			
			List<String> lowercase = names.stream().map(String::toLowerCase).collect(Collectors.toList());
			System.out.println("Lowercase Strings: " + lowercase);
		}
	}