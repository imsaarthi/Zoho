package com.yourcompany.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakInfoResponse {
    private LocalDateTime breakIn;
    private LocalDateTime breakOut;
    private String breakInPhotoUrl;
    private String breakOutPhotoUrl;
    private Integer breakDurationMinutes;

}
