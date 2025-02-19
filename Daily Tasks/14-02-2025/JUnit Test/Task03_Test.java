package com.test;
import com.example.JUnit_Task3;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class Task03_Test {
	private JUnit_Task3 obj;
	

    @Before
    public void setUp() {
        obj = new JUnit_Task3();
        System.out.println("Setup: Creating Task3 instance");
    }

    @After
    public void tearDown() {
        obj = null;
        System.out.println("Teardown: Cleaning up resources");
    }

    @Test
    public void testMultiply() {
        assertEquals(20, obj.multiply(4, 5));
    }

    @Test
    public void testIsEven() {
        assertTrue(obj.isEven(8));
        assertFalse(obj.isEven(7));
    }
}
