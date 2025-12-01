package com.yourcompany.hrms.service;

import com.yourcompany.hrms.dto.UserCreateRequest;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.dto.UserUpdateRequest;
import com.yourcompany.hrms.entity.Organization;
import com.yourcompany.hrms.entity.Role;
import com.yourcompany.hrms.entity.RoleName;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.mapper.UserMapper;
import com.yourcompany.hrms.repository.OrganizationRepository;
import com.yourcompany.hrms.repository.RoleRepository;
import com.yourcompany.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee code already in use");
        }

        if (request.getRoleId() == null) {
            throw new IllegalArgumentException("Role ID is required");
        }
        Role role = roleRepository.findById(java.util.Objects.requireNonNull(request.getRoleId()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (request.getOrgId() == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        Organization organization = organizationRepository
                .findById(java.util.Objects.requireNonNull(request.getOrgId()))
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setOrganization(organization);
        user.setLatitude(user.getLatitude());
        user.setLongitude(user.getLongitude());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toListResponse(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        java.util.Objects.requireNonNull(id, "ID must not be null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        java.util.Objects.requireNonNull(id, "ID must not be null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        userMapper.updateEntity(user, request);

        // Handle fields not in mapper's updateEntity if needed, or update mapper.
        // For now assuming mapper handles common fields.
        // Admin specific fields like role, employeeCode might need special handling if
        // allowed to update.
        // The UserMapper.updateEntity I created in step 48 handles: email, fullName,
        // phone, department, designation, dateOfJoining, employmentType, isActive.
        // It does NOT handle: username, employeeCode, roleName, profileImageUrl.
        // I should update UserMapper to handle these or handle them here.
        // I'll handle them here for now or update mapper. Updating mapper is better.

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmployeeCode() != null) {
            user.setEmployeeCode(request.getEmployeeCode());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getRoleName() != null) {
            RoleName roleName = RoleName.valueOf(request.getRoleName().toUpperCase());
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        java.util.Objects.requireNonNull(id, "ID must not be null");
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public String getCurrentUserEmail() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public com.yourcompany.hrms.dto.ProfilePhotoResponse updateProfilePhoto(Long userId,
            org.springframework.web.multipart.MultipartFile file) {
        java.util.Objects.requireNonNull(userId, "User ID must not be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete old photo if exists
        if (user.getProfileImageUrl() != null) {
            fileStorageService.deleteFileIfExists(user.getProfileImageUrl());
        }

        // Save new photo
        String photoUrl = fileStorageService.saveProfileImage(file, userId);
        user.setProfileImageUrl(photoUrl);
        userRepository.save(user);

        return com.yourcompany.hrms.dto.ProfilePhotoResponse.builder()
                .userId(userId)
                .profileImageUrl(photoUrl)
                .build();
    }
}
