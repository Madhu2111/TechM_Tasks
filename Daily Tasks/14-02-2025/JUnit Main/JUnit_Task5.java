package com.example;

public class JUnit_Task5 {
	public void delayedMethod() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
