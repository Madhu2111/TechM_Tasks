package com.test;
import com.example.JUnit_Task7;
import org.junit.Test;
import static org.junit.Assert.*;

public class Task07_Test {
	JUnit_Task7 obj = new JUnit_Task7();

    @Test
    public void testAdd() {
        int result = obj.add(3, 4);
        assertEquals("Expected sum of 3 and 4 to be 7", 7, result);
    }

    @Test
    public void testIsPositive() {
        assertTrue("Expected 5 to be positive", obj.isPositive(5));
        assertFalse("Expected -3 to be non-positive", obj.isPositive(-3));
    }

}
