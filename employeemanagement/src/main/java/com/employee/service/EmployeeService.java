package com.employee.service;

import java.util.List;

import com.employee.model.Employee;

public interface EmployeeService {
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);
    Employee getEmployeeById(Long id);
    List<Employee> getAllEmployees();
}
