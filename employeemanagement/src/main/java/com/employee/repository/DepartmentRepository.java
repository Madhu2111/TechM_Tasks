package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // You can add findByName or other queries if needed
}