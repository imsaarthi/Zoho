package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.dto.OrganizationRequest;
import com.yourcompany.hrms.dto.OrganizationResponse;
import com.yourcompany.hrms.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<OrganizationResponse>> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Organization created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<OrganizationResponse>> getOrganizationById(@PathVariable Long id) {
        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<OrganizationResponse>>> getAllOrganizations() {
        List<OrganizationResponse> response = organizationService.getAllOrganizations();
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<OrganizationResponse>> updateOrganization(@PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Organization updated successfully", response));
    }
}
