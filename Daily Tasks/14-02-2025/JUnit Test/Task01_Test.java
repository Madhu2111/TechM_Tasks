package com.test;
import com.example.JUnit_Task1;
//CalculatorTest.java

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertEquals;

public class Task01_Test {

 @Test
 public void testProduct() {

     JUnit_Task1 obj = new JUnit_Task1();

     int result = obj.product(3, 5);

     assertEquals(15, result); 
 }

 public static void main(String[] args) {
     Result result = JUnitCore.runClasses(Task01_Test.class);
     if (result.getFailureCount() > 0) {
         System.out.println("Test failed:");
         for (Failure failure : result.getFailures()) {
             System.out.println(failure.toString());
         }
     } else {
         System.out.println("All tests passed successfully.");
     }
 }
}

