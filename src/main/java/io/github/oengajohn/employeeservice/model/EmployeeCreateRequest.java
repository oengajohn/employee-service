package io.github.oengajohn.employeeservice.model;

import java.time.LocalDate;

import io.github.oengajohn.employeeservice.entity.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeCreateRequest {
     
    @Past(message = "birth date needs to past")
    private LocalDate birthDate;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    private Gender gender;
   
    @PastOrPresent(message = "hire date needs to past or present")
    private LocalDate hireDate;
}
