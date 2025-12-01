package com.yourcompany.hrms.mapper;

import com.yourcompany.hrms.dto.LeaveApplyRequest;
import com.yourcompany.hrms.dto.LeaveRequestResponse;
import com.yourcompany.hrms.entity.LeaveRequest;
import com.yourcompany.hrms.entity.LeaveStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaveMapper {

    public LeaveRequest toEntity(LeaveApplyRequest request) {
        if (request == null) {
            return null;
        }
        return LeaveRequest.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .leaveType(request.getLeaveType())
                .status(LeaveStatus.PENDING) // Default status
                .build();
    }

    public LeaveRequestResponse toResponse(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            return null;
        }
        return LeaveRequestResponse.builder()
                .id(leaveRequest.getId())
                .userId(leaveRequest.getUser() != null ? leaveRequest.getUser().getId() : null)
                .userFullName(leaveRequest.getUser() != null ? leaveRequest.getUser().getFullName() : null)
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .status(leaveRequest.getStatus())
                .leaveType(leaveRequest.getLeaveType())
                .reason(leaveRequest.getReason())
                .build();
    }

    public List<LeaveRequestResponse> toListResponse(List<LeaveRequest> leaveRequests) {
        if (leaveRequests == null) {
            return List.of();
        }
        return leaveRequests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
