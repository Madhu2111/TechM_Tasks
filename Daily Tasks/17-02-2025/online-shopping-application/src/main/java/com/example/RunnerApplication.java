package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunnerApplication {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        Cart cart = (Cart) context.getBean("cart");

        System.out.println("Online Shopping Products:");
        cart.showProducts();
    }

}
