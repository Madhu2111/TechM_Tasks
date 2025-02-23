package com.example;

public class Item {
	private String name;
    private double price;
    private int quantity;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public double getPrice() {
    	return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
    	return quantity;
    }

    public void displayItem() {
        System.out.println("Product Name: " + name + ", Price: " + price + ", Quantity: " + quantity);
    }

}
