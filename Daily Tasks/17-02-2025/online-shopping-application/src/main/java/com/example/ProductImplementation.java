package com.example;

public class ProductImplementation implements Product {

	 private String name;
	    private double price;
	    private int quantity;

	    // Setter Injection
	    public void setName(String name) {
	        this.name = name;
	    }

	    public void setPrice(double price) {
	        this.price = price;
	    }

	    public void setQuantity(int quantity) {
	        this.quantity = quantity;
	    }

	    @Override
	    public void displayProduct() {
	        System.out.println("Product Name: " + name + ", Price: " + price + ", Quantity: " + quantity);
	    }
}
