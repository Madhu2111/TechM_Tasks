package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Scanner;
public class StudentRunner {
	public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        
        Student st = (Student) context.getBean("stuBean");

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Student Details:");
        System.out.print("ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        System.out.print("Name: ");
        String name = sc.nextLine();
        
        System.out.print("Department: ");
        String dept = sc.nextLine();

        st.setId(id);
        st.setName(name);
        st.setDept(dept);

        System.out.println("\n Student Details ");
        st.display();
    }
}
