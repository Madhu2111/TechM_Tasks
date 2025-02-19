package com.test;
import com.example.JUnit_Task10_Component1;
import com.example.JUnit_Task10_Component2;
import org.junit.Test;
import static org.junit.Assert.*;

public class Task10_Test {
	private final JUnit_Task10_Component1 repository = new JUnit_Task10_Component1();
    private final JUnit_Task10_Component2 service = new  JUnit_Task10_Component2(repository);

    @Test
    public void testProcessAndSave() {
        String result = service.processAndSave(1, "hello");
        assertEquals("HELLO", result);
        assertEquals("HELLO", service.getProcessedValue(1));
    }

    @Test
    public void testRetrieveNonExisting() {
        assertNull("Should return null for non-existing ID", service.getProcessedValue(99));
    }

}
