package com.example;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EmployeeRunner {
	
public static void main(String args[]) {
	//include the XML file
	ApplicationContext ctx=new ClassPathXmlApplicationContext("config.xml");
	//inclusion of DAO file
	EmployeeDao dao=(EmployeeDao)ctx.getBean("edao");
	//insert query
	
	Scanner sc=new Scanner(System.in);
	
	System.out.println("enter your choice:");
	System.out.println("1.Insert 2.Update 3.Delete");
	int choice=sc.nextInt();
	switch(choice) {
	case 1 : insert(sc,dao);
	break;
	case 2:
		update(sc,dao);
		break;
	case 3:
		delete(sc,dao);
		break;
	default:
		System.out.println("enter correct choice");
	
	}
	 
  }

private static void insert(Scanner sc,EmployeeDao dao) {
	// TODO Auto-generated method stub
	System.out.println("Enter id:");
	int id=sc.nextInt();
	sc.nextLine();
	System.out.println("Enter name:");
	String name=sc.nextLine();
	System.out.println("Enter designation:");
    String desig=sc.nextLine();
    int status=dao.saveEmployee(new Employee(id,name,desig));
	System.out.println("Row Inserted Successfully" +status);
}
private static void update(Scanner sc,EmployeeDao dao) {
	// TODO Auto-generated method stub
	System.out.println("Enter id:");
	int id=sc.nextInt();
	sc.nextLine();
	System.out.println("Enter name:");
	String name=sc.nextLine();
	System.out.println("Enter designation:");
    String desig=sc.nextLine();
    int status=dao.updateEmployee(new Employee(id,name,desig));
	System.out.println("Row updated Successfully" +status);
}
private static void delete(Scanner sc, EmployeeDao dao) {
    System.out.println("Enter ID:");
    int id = sc.nextInt();

    int status = dao.deleteEmployee(id); 
    System.out.println("Row Deleted Successfully: " + status);
}
}
