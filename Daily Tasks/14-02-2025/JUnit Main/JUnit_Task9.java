package com.example;

public class JUnit_Task9 {
	private static volatile JUnit_Task9 instance;

    private JUnit_Task9() {}

    public static JUnit_Task9 getInstance() {
        if (instance == null) {
            synchronized (JUnit_Task9.class) {
                if (instance == null) {
                    instance = new JUnit_Task9();
                }
            }
        }
        return instance;
    }

}
