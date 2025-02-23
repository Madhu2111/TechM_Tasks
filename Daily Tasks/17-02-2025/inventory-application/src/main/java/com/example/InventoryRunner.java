package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InventoryRunner {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");

        Inventory inventory = (Inventory) context.getBean("inventory");
        inventory.displayInventory();
    }

}
