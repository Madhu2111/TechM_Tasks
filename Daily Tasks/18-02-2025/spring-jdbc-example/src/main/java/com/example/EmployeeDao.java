package com.example;

import org.springframework.jdbc.core.JdbcTemplate;


public class EmployeeDao {
 private JdbcTemplate jdbcTemplate;
 
 public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
	   this.jdbcTemplate=jdbcTemplate;
 }
 //insert the row/s into the table.
 public int saveEmployee(Employee e) {
	   String query= "insert into employee values('"+e.getId()+"','"+e.getName()+"','"+e.getDesig()+"')";
	   return jdbcTemplate.update(query);
 }
 //update the row/s into the table
 public int updateEmployee(Employee e) {
	   String query="update employee set name='"+e.getName()+"',designation='"+e.getDesig()+"' where id='"+e.getId()+"' ";
	   return jdbcTemplate.update(query);
 }
//deletion of row/s into the table.
 public int deleteEmployee(int id) { 
     String query = "DELETE FROM employee WHERE empid = ?";
     return jdbcTemplate.update(query, id);
 }

}