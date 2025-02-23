package com.example;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ItemRunner {
	public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");

        System.out.println("Items:");
        ((Item) context.getBean("apple")).displayItem();
        ((Item) context.getBean("banana")).displayItem();
        ((Item) context.getBean("orange")).displayItem();
        ((Item) context.getBean("grape")).displayItem();
    }

}
