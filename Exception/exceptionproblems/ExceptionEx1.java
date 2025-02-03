package com.exceptionproblems;

public class ExceptionEx1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a=10;
		int b=0;
		try {
			int c=a/b;
		}
		catch(Exception e){
			System.out.println("error"+e);
		}
		finally {
			b++;
			System.out.println("The value of a/b :"+a/b);
		}

	}

}
