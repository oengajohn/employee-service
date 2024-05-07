package io.github.oengajohn.employeeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.oengajohn.employeeservice.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    
}
