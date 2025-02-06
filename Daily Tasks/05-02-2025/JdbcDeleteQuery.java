package com.jdbcexamples;

import java.sql.*;
import java.util.Scanner;

public class JdbcDeleteQuery {

	public static void main(String[] args) throws SQLException,Exception {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the employee name to be deleted:");
		String deleteName=sc.next();
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/techm?autoReconnect=true&useSSL=false","root","madhu");
		PreparedStatement pst=con.prepareStatement("delete from employee   where empname=?");
		pst.setString(1, deleteName);
		int value=pst.executeUpdate();
		System.out.println("update success");
		con.close();
		pst.close();

	}

}
