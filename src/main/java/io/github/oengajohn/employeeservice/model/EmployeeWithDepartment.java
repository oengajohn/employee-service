package io.github.oengajohn.employeeservice.model;

import java.time.LocalDate;

import io.github.oengajohn.employeeservice.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeWithDepartment {
    private Integer employeeNumber;

    private LocalDate birthDate;

    private String firstName;

    private String lastName;

    private Gender gender;

    private LocalDate hireDate;

    private Integer departmentId;

    private DepartmentResponse departmentResponse;
}
