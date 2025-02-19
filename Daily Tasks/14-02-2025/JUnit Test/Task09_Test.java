package com.test;
import com.example.JUnit_Task9;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.*;

public class Task09_Test {
	@Test
    public void testSingletonThreadSafety() throws Exception {
        Callable<JUnit_Task9> obj = JUnit_Task9::getInstance;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Future<JUnit_Task9> future1 = executor.submit(obj);
        Future<JUnit_Task9> future2 = executor.submit(obj);
        Future<JUnit_Task9> future3 = executor.submit(obj);

        JUnit_Task9 instance1 = future1.get();
        JUnit_Task9 instance2 = future2.get();
        JUnit_Task9 instance3 = future3.get();

        executor.shutdown();

        assertSame("All instances should be the same", instance1, instance2);
        assertSame("All instances should be the same", instance2, instance3);
    }

}
