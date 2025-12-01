package com.yourcompany.hrms.mapper;

import com.yourcompany.hrms.dto.OrganizationRequest;
import com.yourcompany.hrms.dto.OrganizationResponse;
import com.yourcompany.hrms.entity.Organization;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrganizationMapper {

    public Organization toEntity(OrganizationRequest request) {
        if (request == null) {
            return null;
        }
        return Organization.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .build();
    }

    public void updateEntity(Organization organization, OrganizationRequest request) {
        if (request == null || organization == null) {
            return;
        }
        organization.setName(request.getName());
        organization.setEmail(request.getEmail());
        organization.setPhone(request.getPhone());
        organization.setAddress(request.getAddress());
        organization.setCity(request.getCity());
        organization.setCountry(request.getCountry());
    }

    public OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .email(organization.getEmail())
                .phone(organization.getPhone())
                .address(organization.getAddress())
                .city(organization.getCity())
                .country(organization.getCountry())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }

    public List<OrganizationResponse> toListResponse(List<Organization> organizations) {
        if (organizations == null) {
            return List.of();
        }
        return organizations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
