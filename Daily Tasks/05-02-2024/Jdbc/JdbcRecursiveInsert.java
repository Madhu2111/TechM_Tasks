package com.jdbcexamples;
import java.sql.*;
import java.util.Scanner;
public class JdbcRecursiveInsert {

	public static void main(String[] args) throws SQLException,Exception {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/techm?autoReconnect=true&useSSL=false","root","madhu");
		
		System.out.println("Enter the number of records to be inserted:");
		int n=sc.nextInt();
		int empid = 0;
		String empname = null;
		String empdesig = null;
		
		for(int i=0;i<n;i++) {
			System.out.println("Enter "+ i+" employee details:");
		System.out.println("Enter emp id:");
		empid=sc.nextInt();
		System.out.println("Enter emp name");
		empname=sc.next();
		System.out.println("Enter emp designation");
	    empdesig=sc.next();
	    PreparedStatement pst=con.prepareStatement("insert into employee values(?,?,?)");
		pst.setInt(1,empid);
		pst.setString(2, empname);
		pst.setString(3, empdesig);
		int value=pst.executeUpdate();
		System.out.println(value+ "row inserted");
		if(i==n-1) {
			pst.close();
		}
		}
		
		
		con.close();
	}

}
