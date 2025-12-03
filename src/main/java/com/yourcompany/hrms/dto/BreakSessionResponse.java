package com.yourcompany.hrms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class BreakSessionResponse {
    private Long id;
    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;
    private String breakStartPhotoUrl;
    private String breakEndPhotoUrl;
    private Integer durationMinutes;
    private Double breakEndLat;
    private Double breakEndLon;
    private Double breakStartLat;
    private Double breakStartLon;
}
