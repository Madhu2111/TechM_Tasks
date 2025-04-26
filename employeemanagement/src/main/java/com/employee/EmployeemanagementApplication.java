package com.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.EmployeeRepository;
import java.util.Arrays;

@SpringBootApplication
public class EmployeemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeemanagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataInitializer(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
		return args -> {
			if (departmentRepository.count() == 0 && employeeRepository.count() == 0) {
				Department dept1 = Department.builder().name("Engineering").build();
				Department dept2 = Department.builder().name("HR").build();
				departmentRepository.saveAll(Arrays.asList(dept1, dept2));

				Employee emp1 = Employee.builder().name("Alice Smith").email("alice@example.com").salary(70000).department(dept1).build();
				Employee emp2 = Employee.builder().name("Bob Johnson").email("bob@example.com").salary(65000).department(dept1).build();
				Employee emp3 = Employee.builder().name("Carol White").email("carol@example.com").salary(60000).department(dept2).build();
				employeeRepository.saveAll(Arrays.asList(emp1, emp2, emp3));
			}
		};
	}

}
