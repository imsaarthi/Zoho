package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private com.yourcompany.hrms.entity.LeaveType leaveType;
    private String reason;
}
