package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceDaySummaryResponse {
    private LocalDate date;
    private LocalDateTime firstCheckIn;
    private LocalDateTime lastCheckOut;
    private Long totalWorkMinutes;
    private Double totalWorkHours;
    private AttendanceStatus status;
    private Double latitude;
    private Double longitude;
}
