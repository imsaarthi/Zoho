package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "Organization ID is required")
    private Long orgId;

    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private Double latitude;
    private Double longitude;
    private EmploymentType employmentType;
}
