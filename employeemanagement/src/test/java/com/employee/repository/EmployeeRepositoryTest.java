package com.employee.repository;

import com.employee.model.Department;
import com.employee.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldSaveEmployee() {
        // given
        Department department = Department.builder().name("IT").build();
        entityManager.persist(department);

        Employee employee = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(50000)
                .department(department)
                .build();

        // when
        Employee savedEmployee = employeeRepository.save(employee);

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldFindEmployeeById() {
        // given
        Department department = Department.builder().name("IT").build();
        entityManager.persist(department);

        Employee employee = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(50000)
                .department(department)
                .build();
        entityManager.persist(employee);

        // when
        Optional<Employee> found = employeeRepository.findById(employee.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(employee.getName());
    }

    @Test
    void shouldFindAllEmployees() {
        // given
        Department department = Department.builder().name("IT").build();
        entityManager.persist(department);

        Employee emp1 = Employee.builder().name("John").email("john@example.com").salary(50000).department(department).build();
        Employee emp2 = Employee.builder().name("Jane").email("jane@example.com").salary(60000).department(department).build();

        entityManager.persist(emp1);
        entityManager.persist(emp2);

        // when
        List<Employee> employees = employeeRepository.findAll();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void shouldDeleteEmployee() {
        // given
        Department department = Department.builder().name("IT").build();
        entityManager.persist(department);

        Employee employee = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(50000)
                .department(department)
                .build();
        entityManager.persist(employee);

        // when
        employeeRepository.deleteById(employee.getId());

        // then
        Optional<Employee> deleted = employeeRepository.findById(employee.getId());
        assertThat(deleted).isEmpty();
    }
}