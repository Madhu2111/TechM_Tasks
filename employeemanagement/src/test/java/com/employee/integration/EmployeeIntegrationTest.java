package com.employee.integration;

import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        department = Department.builder()
                .name("IT")
                .build();
        department = departmentRepository.save(department);

        employee = Employee.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(50000)
                .department(department)
                .build();
    }

    @Test
    void shouldCreateAndRetrieveEmployee() throws Exception {
        // Create employee
        String employeeJson = objectMapper.writeValueAsString(employee);
        
        String responseContent = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.department.name").value("IT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Employee createdEmployee = objectMapper.readValue(responseContent, Employee.class);

        // Retrieve employee
        mockMvc.perform(get("/api/employees/" + createdEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        // Create employee first
        Employee savedEmployee = employeeRepository.save(employee);

        // Update employee
        savedEmployee.setName("John Updated");
        savedEmployee.setEmail("john.updated@example.com");
        savedEmployee.setSalary(55000);

        mockMvc.perform(put("/api/employees/" + savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.salary").value(55000));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        // Create employee first
        Employee savedEmployee = employeeRepository.save(employee);

        // Delete employee
        mockMvc.perform(delete("/api/employees/" + savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify employee list is empty
        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldHandleInvalidEmployeeData() throws Exception {
        employee.setEmail("invalid-email"); // Invalid email format

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleNonExistentEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}