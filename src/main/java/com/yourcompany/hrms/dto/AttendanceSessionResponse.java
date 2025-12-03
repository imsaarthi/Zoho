package com.yourcompany.hrms.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AttendanceSessionResponse {
    private Long userId;
    private Long id;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer durationMinutes;
    private String checkInPhotoUrl;
    private String checkOutPhotoUrl;
    private Double checkInLon;
    private Double checkInLat;

    private java.util.List<BreakSessionResponse> breaks;
}
