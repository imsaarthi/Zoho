package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.LeaveType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponse {
    private LeaveType leaveType;
    private int totalLeaves;
    private int usedLeaves;
    private int pendingLeaves;
    private int availableLeaves;
}
