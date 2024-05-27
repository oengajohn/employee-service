package io.github.oengajohn.employeeservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.oengajohn.employeeservice.entity.Employee;
import io.github.oengajohn.employeeservice.model.DepartmentResponse;
import io.github.oengajohn.employeeservice.model.EmployeeCreateRequest;
import io.github.oengajohn.employeeservice.model.EmployeeResponse;
import io.github.oengajohn.employeeservice.model.EmployeeWithDepartment;
import io.github.oengajohn.employeeservice.repository.EmployeeRepository;
import io.github.oengajohn.employeeservice.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;
    private final RestClient restClient;
    private final RestTemplate restTemplate;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper, WebClient webClient,
            RestClient restClient, RestTemplate restTemplate) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
        this.restClient = restClient;
        this.restTemplate = restTemplate;
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
                    // DepartmentResponse dpt = getDepartmentUsingWebClient(emp);
                    // DepartmentResponse dpt = getDepartmentUsingRestClient(emp);
                    DepartmentResponse dpt = getDepartmentUsingRestTemplate(emp);
                    emp.setDepartmentResponse(dpt);
                });
        return employeesList;
    }

    private DepartmentResponse getDepartmentUsingWebClient(EmployeeWithDepartment emp) {
        log.info("Making the call via WebClient");
        return webClient.get()
                .uri("http://localhost:8081/api/department/" + emp.getDepartmentId())
                .retrieve()
                .bodyToMono(DepartmentResponse.class)
                .block();
    }

    private DepartmentResponse getDepartmentUsingRestClient(EmployeeWithDepartment emp) {
        log.info("Making the call via RestClient");
        return restClient.get()
                .uri("http://localhost:8081/api/department/" + emp.getDepartmentId())
                .retrieve()
                .body(DepartmentResponse.class);

    }

    private DepartmentResponse getDepartmentUsingRestTemplate(EmployeeWithDepartment emp) {
        log.info("Making the call via RestTemplate");
        return restTemplate.getForObject("http://localhost:8081/api/department/" + emp.getDepartmentId(),
                DepartmentResponse.class);

    }

}
