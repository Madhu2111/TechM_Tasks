package com.test;
import com.example.JUnit_Task5;
import org.junit.Test;

public class Task05_Test {
	JUnit_Task5 obj = new JUnit_Task5();

    @Test(timeout = 600)
    public void testDelayedMethodTimeout() {
        obj.delayedMethod();
    }

}
