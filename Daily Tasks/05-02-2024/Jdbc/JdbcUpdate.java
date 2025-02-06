package com.jdbcexamples;

import java.sql.*;
import java.util.Scanner;

public class JdbcUpdate {

	public static void main(String[] args) throws SQLException,Exception {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the updated name:");
		String updated=sc.next();
		System.out.println("Enter the name to be updated :");
		String original=sc.next();
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/techm?autoReconnect=true&useSSL=false","root","madhu");
		PreparedStatement pst=con.prepareStatement("update employee set empname=?  where empname=?");
		pst.setString(1, updated);
		pst.setString(2, original);
		int value=pst.executeUpdate();
		System.out.println("update success");
		con.close();
		pst.close();
	}

}
