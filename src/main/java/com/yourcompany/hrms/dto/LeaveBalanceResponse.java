package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.LeaveType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponse {
    private Long id;
    private Long userId;
    private LeaveType leaveType;
    private Double totalLeaves;
    private Double usedLeaves;
    private Double pendingLeaves;
    private Double availableLeaves;
}
