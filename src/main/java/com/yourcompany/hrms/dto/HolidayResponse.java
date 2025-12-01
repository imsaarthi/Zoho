package com.yourcompany.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponse {

    private Long id;
    private String title;
    private LocalDate date;
    private Boolean recurring;
    private String description;
    private Long organizationId;
}
