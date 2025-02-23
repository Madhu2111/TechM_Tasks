package com.example;

import java.util.List;

public class Cart {
	private List<Product> products;

    // Interface Injection (Setter)
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void showProducts() {
        for (Product product : products) {
            product.displayProduct();
        }
    }

}
