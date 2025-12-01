package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.AttendanceType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceActionRequest {

    @NotNull(message = "Attendance type is required")
    private AttendanceType attendanceType;

    private Double latitude;
    private Double longitude;

    @NotNull(message = "File is required")
    private org.springframework.web.multipart.MultipartFile file;
}
