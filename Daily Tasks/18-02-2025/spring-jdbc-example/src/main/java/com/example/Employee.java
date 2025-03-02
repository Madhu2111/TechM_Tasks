package com.example;

public class Employee {
	private int id;
	private String name;
	private String desig;
	
	public Employee() {}
	public Employee(int id,String name,String desig) {
		this.id=id;
		this.name=name;
		this.desig=desig;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDesig() {
		return desig;
	}
	
	public void setId(int id) {
		this.id=id;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setDesig(String desig) {
		this.desig=desig;
	}
	
}
