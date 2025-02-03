package com.ThreadExamples;

public class ThreadEx1 extends Thread {
	
	public void run() {
		System.out.println("Hello World!");
		
	}
	public static void main(String[] args) {
		ThreadEx1 obj=new ThreadEx1();
		obj.run();
	}

}
