package com.yourcompany.hrms.mapper;

import com.yourcompany.hrms.dto.HolidayRequest;
import com.yourcompany.hrms.dto.HolidayResponse;
import com.yourcompany.hrms.entity.Holiday;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HolidayMapper {

    public Holiday toEntity(HolidayRequest request) {
        if (request == null) {
            return null;
        }
        return Holiday.builder()
                .title(request.getTitle())
                .date(request.getDate())
                .recurring(request.getRecurring())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(Holiday holiday, HolidayRequest request) {
        if (request == null || holiday == null) {
            return;
        }
        holiday.setTitle(request.getTitle());
        holiday.setDate(request.getDate());
        holiday.setRecurring(request.getRecurring());
        holiday.setDescription(request.getDescription());
    }

    public HolidayResponse toResponse(Holiday holiday) {
        if (holiday == null) {
            return null;
        }
        return HolidayResponse.builder()
                .id(holiday.getId())
                .title(holiday.getTitle())
                .date(holiday.getDate())
                .recurring(holiday.getRecurring())
                .description(holiday.getDescription())
                .organizationId(holiday.getOrganization() != null ? holiday.getOrganization().getId() : null)
                .build();
    }

    public List<HolidayResponse> toListResponse(List<Holiday> holidays) {
        if (holidays == null) {
            return List.of();
        }
        return holidays.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
