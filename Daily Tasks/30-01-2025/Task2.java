package com.examples;

import java.util.Scanner;

public class Task2 {
	
	public static void main(String args[]) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the number of students required: ");
		int n = sc.nextInt();

		int i,m1,m2,m3,m4;
		double avg[] = new double[n];
		
		int[] arr = new int[n];
		
		for(i=0;i<n;i++) {
			arr[i] = i+1;
			System.out.println("\nEnter Student"+(i+1)+" Details:");
			System.out.println("Enter m1 marks: ");
			m1 = sc.nextInt();
			System.out.println("Enter m2 marks: ");
			m2 = sc.nextInt();
			System.out.println("Enter m3 marks: ");
			m3 = sc.nextInt();
			System.out.println("Enter m4 marks: ");
			m4 = sc.nextInt();
			avg[i]=(m1+m2+m3+m4)/4;
		}
		    
		double temp = 0; 
		int t = 0;
		for(i=0; i < n; i++){    
			for(int j=1; j < (n-i); j++){    
				if(avg[j-1] < avg[j]){    
					temp = avg[j-1];    
					avg[j-1] = avg[j];    
					avg[j] = temp;
					
					t = arr[j-1];    
					arr[j-1] = arr[j];    
					arr[j] = t;
				}          
			}
		}
		
		System.out.println("\nSNo. Name     Marks Avg");
		for(i=0;i<n;i++) {
			System.out.println((i+1)+".   Student"+arr[i]+" "+(avg[i]*4)+" "+avg[i]);
		}
		
		sc.close();
	}

}
