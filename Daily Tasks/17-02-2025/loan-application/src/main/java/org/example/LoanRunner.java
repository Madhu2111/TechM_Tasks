package org.example;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.example.Loan;

public class LoanRunner {
	public static void main(String args[]) {
		Resource res=new ClassPathResource("config.xml");
		@SuppressWarnings("deprecation")
		BeanFactory factory=new XmlBeanFactory(res);
		Loan l=(Loan)factory.getBean("housebean");
		Loan l1=(Loan)factory.getBean("vehiclebean");
		
		System.out.println("House Loan Details:\n");
		l.calculateEMI();
		l.displayLoanDetails();
		System.out.println("Vehicle Loan Details:\n");
		l1.calculateEMI();
		l1.displayLoanDetails();
	}
}
