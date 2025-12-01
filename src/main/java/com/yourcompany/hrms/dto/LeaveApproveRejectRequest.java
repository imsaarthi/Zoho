package com.yourcompany.hrms.dto;

import com.yourcompany.hrms.entity.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApproveRejectRequest {

    @NotNull(message = "Status is required")
    private LeaveStatus status;
}
