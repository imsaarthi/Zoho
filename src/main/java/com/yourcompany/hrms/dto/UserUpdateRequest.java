package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private EmploymentType employmentType;
    private Boolean isActive;

    private String username;
    private String employeeCode;
    private String roleName;
    private String profileImageUrl;
}
