package com.yourcompany.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceActionResponse {
    private Long sessionId;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private List<BreakInfoResponse> breaks; // Changed type from BreakInfo to BreakInfoResponse
    private Integer totalWorkMinutes;
    private String attendanceStatus; // Changed type from AttendanceStatus enum to String
    private String photoUrl; // Added field
    private Double latitude;
    private Double longitude;
    private Double ipaddress;
    private Double checkInLon;
    private Double checkInLat;
}
