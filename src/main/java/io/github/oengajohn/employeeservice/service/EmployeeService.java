package io.github.oengajohn.employeeservice.service;

import io.github.oengajohn.employeeservice.model.EmployeeCreateRequest;
import io.github.oengajohn.employeeservice.model.EmployeeResponse;
import jakarta.validation.Valid;

public interface EmployeeService {

    EmployeeResponse createEmployee(@Valid EmployeeCreateRequest employeeCreateRequest);
    
}
