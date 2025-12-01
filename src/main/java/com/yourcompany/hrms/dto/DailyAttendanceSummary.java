package com.yourcompany.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAttendanceSummary {
    private LocalDate date;
    private long totalMinutes;
    private double totalHours;
    private List<AttendanceSessionResponse> sessions;
}
