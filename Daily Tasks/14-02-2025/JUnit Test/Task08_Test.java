package com.test;
import com.example.JUnit_Task8;
import org.junit.Test;
import static org.junit.Assert.*;

public class Task08_Test {
	private final JUnit_Task8 obj = new JUnit_Task8();

    @Test
    public void testGetSquare() {
        assertEquals("Expected square of 5 to be 125", 125, obj.getCube(5));
    }

}
