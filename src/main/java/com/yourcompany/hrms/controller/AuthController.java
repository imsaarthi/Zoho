package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.dto.AuthenticationRequest;
import com.yourcompany.hrms.dto.AuthenticationResponse;
import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.jwt.JwtService;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final UserService userService;

        @PostMapping("/login")
        public ResponseEntity<ResponseWrapper<AuthenticationResponse>> login(
                        @Valid @RequestBody AuthenticationRequest request) {
                // Authenticate user credentials using AuthenticationManager
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                // We need User entity for JWT generation
                User user = userService.findByEmail(request.getEmail());

                // Generate JWT token
                String token = jwtService.generateToken(user);

                // Get UserResponse DTO
                UserResponse userResponse = userService.getUserResponseByEmail(request.getEmail());

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                                .token(token)
                                .userData(userResponse)
                                .build();

                return ResponseEntity.ok(ResponseWrapper.success("Login successful", authResponse));
        }
}
