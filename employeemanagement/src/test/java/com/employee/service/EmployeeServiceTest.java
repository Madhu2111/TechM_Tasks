package com.employee.service;

import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.repository.EmployeeRepository;
import com.employee.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("IT")
                .build();

        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .salary(50000)
                .department(department)
                .build();
    }

    @Test
    void shouldSaveEmployee() {
        // given
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // when
        Employee savedEmployee = employeeService.saveEmployee(employee);

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("John Doe");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldGetEmployeeById() {
        // given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // when
        Employee found = employeeService.getEmployeeById(1L);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(employee.getName());
        verify(employeeRepository).findById(1L);
    }

    @Test
    void shouldGetAllEmployees() {
        // given
        Employee employee2 = Employee.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .salary(60000)
                .department(department)
                .build();

        when(employeeRepository.findAll()).thenReturn(List.of(employee, employee2));

        // when
        List<Employee> employees = employeeService.getAllEmployees();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Doe");
        verify(employeeRepository).findAll();
    }

    @Test
    void shouldUpdateEmployee() {
        // given
        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@example.com")
                .salary(55000)
                .department(department)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // when
        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getSalary()).isEqualTo(55000);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldDeleteEmployee() {
        // given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).deleteById(1L);

        // when
        employeeService.deleteEmployee(1L);

        // then
        verify(employeeRepository).deleteById(1L);
    }
}