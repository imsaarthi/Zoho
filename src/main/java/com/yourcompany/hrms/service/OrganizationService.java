package com.yourcompany.hrms.service;

import com.yourcompany.hrms.dto.OrganizationRequest;
import com.yourcompany.hrms.dto.OrganizationResponse;
import com.yourcompany.hrms.entity.Organization;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.mapper.OrganizationMapper;
import com.yourcompany.hrms.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Transactional
    public OrganizationResponse createOrganization(OrganizationRequest request) {
        Organization organization = organizationMapper.toEntity(request);
        organization = organizationRepository.save(organization);
        return organizationMapper.toResponse(organization);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        return organizationMapper.toResponse(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationMapper.toListResponse(organizationRepository.findAll());
    }

    @Transactional
    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        if (id == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        organizationMapper.updateEntity(organization, request);
        organization = organizationRepository.save(organization);
        return organizationMapper.toResponse(organization);
    }
}
