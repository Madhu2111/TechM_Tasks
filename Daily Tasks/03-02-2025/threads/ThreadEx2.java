package com.ThreadExamples;

class EvenThread extends Thread {
    public void run() {
        for (int i = 2; i <= 20; i++) {
        	if(i%2==0) {
            System.out.println("Even: " + i);
        	}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class OddThread extends Thread {
    public void run() {
        for (int i = 1; i <= 20; i++) {
        	if(i%2!=0) {
            System.out.println("Odd: " + i);
        	}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class ThreadEx2 {
    public static void main(String[] args) {
        EvenThread evenThread = new EvenThread();
        OddThread oddThread = new OddThread();

        evenThread.start();
        oddThread.start();
    }
}