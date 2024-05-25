package io.github.oengajohn.employeeservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.oengajohn.employeeservice.model.EmployeeCreateRequest;
import io.github.oengajohn.employeeservice.model.EmployeeResponse;
import io.github.oengajohn.employeeservice.model.EmployeeWithDepartment;
import io.github.oengajohn.employeeservice.service.EmployeeService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public EmployeeResponse create(@RequestBody @Valid EmployeeCreateRequest employeeCreateRequest) {
        return employeeService.createEmployee(employeeCreateRequest);
    }

    @GetMapping
    public List<EmployeeResponse> list() {
        return employeeService.listAll();
    }

    @GetMapping("{employeeNumber}")
    public EmployeeResponse findByEmployeeNumber(@PathVariable(value = "employeeNumber") Integer employeeNumber) {
        return employeeService.findByEmployeeNumber(employeeNumber);
    }

    @DeleteMapping("{employeeNumber}")
    public String deleteByEmployeeNumber(@PathVariable(value = "employeeNumber") Integer employeeNumber) {
        return employeeService.deleteByEmployeeNumber(employeeNumber);
    }

    @GetMapping("with-department")
    public List<EmployeeWithDepartment> getEmployeesWithDepartment() {
        return employeeService.getEmployeesWithDepartment();
    }

}
