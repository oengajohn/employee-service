package io.github.oengajohn.employeeservice.service;

import java.util.List;

import io.github.oengajohn.employeeservice.model.EmployeeCreateRequest;
import io.github.oengajohn.employeeservice.model.EmployeeResponse;
import io.github.oengajohn.employeeservice.model.EmployeeWithDepartment;
import jakarta.validation.Valid;

public interface EmployeeService {

    EmployeeResponse createEmployee(@Valid EmployeeCreateRequest employeeCreateRequest);

    List<EmployeeResponse> listAll();

    EmployeeResponse findByEmployeeNumber(Integer employeeNumber);

    String deleteByEmployeeNumber(Integer employeeNumber);

    List<EmployeeWithDepartment> getEmployeesWithDepartment();

}
