package com.yourcompany.hrms.mapper;

import com.yourcompany.hrms.dto.UserCreateRequest;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.dto.UserUpdateRequest;
import com.yourcompany.hrms.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .employeeCode(request.getEmployeeCode())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .dateOfJoining(request.getDateOfJoining())
                .employmentType(request.getEmploymentType())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .build();
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        if (request == null || user == null) {
            return;
        }
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setDepartment(request.getDepartment());
        user.setDesignation(request.getDesignation());
        user.setDateOfJoining(request.getDateOfJoining());
        user.setEmploymentType(request.getEmploymentType());
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .employeeCode(user.getEmployeeCode())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .createdById(user.getCreatedBy() != null ? user.getCreatedBy().getId() : null)
                .createdByFullName(user.getCreatedBy() != null ? user.getCreatedBy().getFullName() : null)
                .roleName(user.getRole() != null ? user.getRole().getName().name() : null)
                .orgId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .username(user.getUsername())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .dateOfJoining(user.getDateOfJoining())
                .employmentType(user.getEmploymentType())
                .lastLogin(user.getLastLogin())
                .profileImageUrl(user.getProfileImageUrl())
                .longitude(user.getLongitude())
                .latitude(user.getLatitude())
                .build();
    }

    public List<UserResponse> toListResponse(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
