package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.dto.UserCreateRequest;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.dto.UserUpdateRequest;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("User created successfully", userResponse));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ResponseWrapper.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ResponseWrapper.success(userResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUser(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("User updated successfully", userResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseWrapper.success("User soft deleted successfully"));
    }
}
