package com.ThreadExamples;

public class ThreadEx2 {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreadEx2 even=new ThreadEx2() {
		
		public void run() {
			for(int i=0;i<20;i++) {
				if(i%2==0) {
					System.out.println(i);
				}
			}
		}
		
		
	};
	ThreadEx2 odd=new ThreadEx2() {
		public void run() {
			for(int i=0;i<20;i++) {
				if(i%2!=0) {
					System.out.println(i);
				}
			}
		}
	};

}
}