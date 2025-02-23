package com.example;

public class Student {
	private int id;
	private String name;
	private String dept;
	public Student() {
		
	}
	public Student(int id,String name,String dept) {
		super();
		this.id=id;
		this.name=name;
		this.dept=dept;
	}
	public void setId(int id) {
		this.id=id;
	}
	public int getId() {
        return id;
    }
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void display() {
        System.out.println("ID: " + id + ", Name: " + name + ", Department: " + dept);
    }

}
