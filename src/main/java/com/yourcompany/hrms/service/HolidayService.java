package com.yourcompany.hrms.service;

import com.yourcompany.hrms.dto.HolidayRequest;
import com.yourcompany.hrms.dto.HolidayResponse;
import com.yourcompany.hrms.entity.Holiday;
import com.yourcompany.hrms.entity.Organization;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.mapper.HolidayMapper;
import com.yourcompany.hrms.repository.HolidayRepository;
import com.yourcompany.hrms.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

        private final HolidayRepository holidayRepository;
        private final OrganizationRepository organizationRepository;
        private final HolidayMapper holidayMapper;

        @Transactional
        public HolidayResponse createHoliday(HolidayRequest request, Long orgId) {
                if (orgId == null) {
                        throw new IllegalArgumentException("Organization ID is required");
                }
                Organization organization = organizationRepository.findById(orgId)
                                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

                Holiday holiday = holidayMapper.toEntity(request);
                holiday.setOrganization(organization);

                Holiday savedHoliday = holidayRepository.save(holiday);
                return holidayMapper.toResponse(savedHoliday);
        }

        @Transactional(readOnly = true)
        public List<HolidayResponse> getAllHolidays(Long orgId) {
                if (orgId == null) {
                        throw new IllegalArgumentException("Organization ID is required");
                }
                return holidayMapper.toListResponse(holidayRepository.findByOrganizationId(orgId));
        }

        @Transactional(readOnly = true)
        public HolidayResponse getHolidayById(Long id) {
                if (id == null) {
                        throw new IllegalArgumentException("Holiday ID is required");
                }
                Holiday holiday = holidayRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));
                return holidayMapper.toResponse(holiday);
        }

        @Transactional
        public HolidayResponse updateHoliday(Long id, HolidayRequest request) {
                if (id == null) {
                        throw new IllegalArgumentException("Holiday ID is required");
                }
                Holiday holiday = holidayRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

                holidayMapper.updateEntity(holiday, request);
                Holiday updatedHoliday = holidayRepository.save(holiday);
                return holidayMapper.toResponse(updatedHoliday);
        }

        @Transactional
        public void deleteHoliday(Long id) {
                if (id == null) {
                        throw new IllegalArgumentException("Holiday ID is required");
                }
                if (!holidayRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Holiday not found");
                }
                holidayRepository.deleteById(id);
        }

        @Transactional(readOnly = true)
        public boolean isHoliday(LocalDate date, Long orgId) {
                return holidayRepository.existsByDateAndOrganizationId(date, orgId);
        }
}
