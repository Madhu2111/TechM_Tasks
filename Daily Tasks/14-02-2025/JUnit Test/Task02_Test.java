package com.test;
import com.example.JUnit_Task2;
import org.junit.Test;
import static org.junit.Assert.*;

public class Task02_Test {
	@Test
    public void testValidateNumberThrowsException() {
        JUnit_Task2 task2 = new JUnit_Task2();
        try {
            task2.validateNumber(-1);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Number must be non-negative", e.getMessage());
        }
    }

}
