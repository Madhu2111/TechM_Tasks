package com.test;
import com.example.JUnit_Task6;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class Task06_Test {
	private final JUnit_Task6 obj = new JUnit_Task6();

    @Test
    public void testDivide() {
        assertEquals(2, obj.divide(10, 5));
    }

    @Ignore("Skipping this test because division by zero handling is not implemented yet")
    @Test
    public void testDivideByZero() {
        obj.divide(10, 0);
    }

}
