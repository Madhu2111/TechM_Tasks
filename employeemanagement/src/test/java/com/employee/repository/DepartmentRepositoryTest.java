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
public class DepartmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void shouldSaveDepartment() {
        // given
        Department department = Department.builder()
                .name("Engineering")
                .build();

        // when
        Department savedDepartment = departmentRepository.save(department);

        // then
        assertThat(savedDepartment).isNotNull();
        assertThat(savedDepartment.getId()).isNotNull();
        assertThat(savedDepartment.getName()).isEqualTo("Engineering");
    }

    @Test
    void shouldFindDepartmentById() {
        // given
        Department department = Department.builder()
                .name("Engineering")
                .build();
        entityManager.persist(department);

        // when
        Optional<Department> found = departmentRepository.findById(department.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(department.getName());
    }

    @Test
    void shouldFindAllDepartments() {
        // given
        Department dept1 = Department.builder().name("Engineering").build();
        Department dept2 = Department.builder().name("HR").build();

        entityManager.persist(dept1);
        entityManager.persist(dept2);

        // when
        List<Department> departments = departmentRepository.findAll();

        // then
        assertThat(departments).hasSize(2);
        assertThat(departments).extracting(Department::getName)
                .containsExactlyInAnyOrder("Engineering", "HR");
    }

    @Test
    void shouldDeleteDepartment() {
        // given
        Department department = Department.builder()
                .name("Engineering")
                .build();
        entityManager.persist(department);

        // when
        departmentRepository.deleteById(department.getId());

        // then
        Optional<Department> deleted = departmentRepository.findById(department.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void shouldSaveDepartmentWithEmployees() {
        // given
        Department department = Department.builder()
                .name("Engineering")
                .build();

        Employee employee1 = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(70000)
                .department(department)
                .build();

        Employee employee2 = Employee.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .salary(75000)
                .department(department)
                .build();

        department.setEmployees(List.of(employee1, employee2));

        // when
        Department savedDepartment = departmentRepository.save(department);

        // then
        assertThat(savedDepartment).isNotNull();
        assertThat(savedDepartment.getEmployees()).hasSize(2);
        assertThat(savedDepartment.getEmployees()).extracting(Employee::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }
}