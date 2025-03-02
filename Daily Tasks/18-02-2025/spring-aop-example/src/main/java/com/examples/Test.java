package com.examples;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

public class Test {
	public static void main(String[] args){

	 BeanFactory factory = new DefaultListableBeanFactory();
     BeanDefinitionRegistry reg = (BeanDefinitionRegistry) factory;
	 XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(reg);
	 reader.loadBeanDefinitions(new ClassPathResource("config.xml"));
	   //calling the bean
	 Operation e = (Operation) factory.getBean("opBean");
		e.showMsg();
		System.out.println("calling msg...");

		System.out.println("calling getNum1...");
		e.getNum1();
		
		System.out.println("calling getNum2...");
		e.getNum2();
	}
}

