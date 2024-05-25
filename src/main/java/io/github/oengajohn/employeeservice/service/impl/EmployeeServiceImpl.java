package io.github.oengajohn.employeeservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.oengajohn.employeeservice.entity.Employee;
import io.github.oengajohn.employeeservice.model.DepartmentResponse;
import io.github.oengajohn.employeeservice.model.EmployeeCreateRequest;
import io.github.oengajohn.employeeservice.model.EmployeeResponse;
import io.github.oengajohn.employeeservice.model.EmployeeWithDepartment;
import io.github.oengajohn.employeeservice.repository.EmployeeRepository;
import io.github.oengajohn.employeeservice.service.EmployeeService;
import jakarta.validation.Valid;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper, WebClient webClient) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
    }

    @Override
    public EmployeeResponse createEmployee(@Valid EmployeeCreateRequest employeeCreateRequest) {
        Employee empReq = modelMapper.map(employeeCreateRequest, Employee.class);
        var savedEmployee = employeeRepository.save(empReq);
        return modelMapper.map(savedEmployee, EmployeeResponse.class);
    }

    @Override
    public List<EmployeeResponse> listAll() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EmployeeResponse.class))
                .toList();
    }

    @Override
    public EmployeeResponse findByEmployeeNumber(Integer employeeNumber) {
        return employeeRepository.findById(employeeNumber)
                .map(e -> modelMapper.map(e, EmployeeResponse.class))
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public String deleteByEmployeeNumber(Integer employeeNumber) {
        employeeRepository.deleteById(employeeNumber);
        return "Success";
    }

    @Override
    public List<EmployeeWithDepartment> getEmployeesWithDepartment() {
        // TODO: fetch the list of employees
        // TODO: Get deparment information for the employee
        // TODO: set the department information

        List<EmployeeWithDepartment> employeesList = employeeRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EmployeeWithDepartment.class))
                .toList();
        // http:localhost:8081/api/department/2
        employeesList.stream()
                .forEach(emp -> {
                    DepartmentResponse dpt = webClient.get()
                            .uri("http://localhost:8081/api/department/" + emp.getDepartmentId())
                            .retrieve()
                            .bodyToMono(DepartmentResponse.class)
                            .block();
                    emp.setDepartmentResponse(dpt);
                });
        return employeesList;
    }

}
