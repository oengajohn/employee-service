package io.github.oengajohn.employeeservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

    @Value("${department-service-host-url}")
    private String departmentServiceHostUrl;

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
        List<EmployeeWithDepartment> employeesList = employeeRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EmployeeWithDepartment.class))
                .toList();
        // ? Solution 1
        // setDepartmentInformationByIndividualCalls(employeesList);
        // return employeesList;
        // ? Solution 2
        return getEmployeesByMakingBatchRequestToDepartmentService(employeesList);
    }

    private List<EmployeeWithDepartment> getEmployeesByMakingBatchRequestToDepartmentService(
            List<EmployeeWithDepartment> employeesList) {

        log.info("Making the call by batch");
        // List<Integer> departmentIds =
        // employeesList.stream().map(emp->emp.getDepartmentId()).toList();
        List<Integer> departmentIds = employeesList.stream().map(EmployeeWithDepartment::getDepartmentId).toList();

        // http://localhost:8081/api/department/batch
        // [1,2,4,7]
        List<DepartmentResponse> departments = restClient.post()
                .uri(departmentServiceHostUrl + "/api/department/batch")
                .body(departmentIds)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DepartmentResponse>>() {
                });
        if (departments == null) {
            departments = new ArrayList<>();
        }
        Map<Integer, DepartmentResponse> departmentMap = departments.stream()
                .collect(Collectors.toMap(DepartmentResponse::getDepartmentNumber, Function.identity()));
        return employeesList.stream()
                .map(emp -> {
                    emp.setDepartmentResponse(departmentMap.get(emp.getDepartmentId()));
                    return emp;
                })
                .toList();
    }

    private void setDepartmentInformationByIndividualCalls(List<EmployeeWithDepartment> employeesList) {
        // http:localhost:8081/api/department/2
        employeesList.stream()
                .forEach(emp -> {
                    // DepartmentResponse dpt = getDepartmentUsingWebClient(emp);
                    // DepartmentResponse dpt = getDepartmentUsingRestTemplate(emp);
                    DepartmentResponse dpt = getDepartmentUsingRestClient(emp);
                    emp.setDepartmentResponse(dpt);
                });
    }

    private DepartmentResponse getDepartmentUsingWebClient(EmployeeWithDepartment emp) {
        log.info("Making the call via WebClient");
        return webClient.get()
                .uri(departmentServiceHostUrl + "/api/department/" + emp.getDepartmentId())
                .retrieve()
                .bodyToMono(DepartmentResponse.class)
                .block();
    }

    private DepartmentResponse getDepartmentUsingRestClient(EmployeeWithDepartment emp) {
        log.info("Making the call via RestClient");
        return restClient.get()
                .uri(departmentServiceHostUrl + "/api/department/" + emp.getDepartmentId())
                .retrieve()
                .body(DepartmentResponse.class);

    }

    private DepartmentResponse getDepartmentUsingRestTemplate(EmployeeWithDepartment emp) {
        log.info("Making the call via RestTemplate");
        return restTemplate.getForObject(departmentServiceHostUrl + "/api/department/" + emp.getDepartmentId(),
                DepartmentResponse.class);

    }

}
