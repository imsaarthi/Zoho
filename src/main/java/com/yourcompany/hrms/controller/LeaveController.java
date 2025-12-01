package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.dto.LeaveApplyRequest;
import com.yourcompany.hrms.dto.LeaveApproveRejectRequest;
import com.yourcompany.hrms.dto.LeaveBalanceResponse;
import com.yourcompany.hrms.dto.LeaveRequestResponse;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.service.LeaveService;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    @PostMapping("/apply")
    public ResponseEntity<ResponseWrapper<LeaveRequestResponse>> applyLeave(
            @Valid @RequestBody LeaveApplyRequest request) {
        String currentUserEmail = userService.getCurrentUserEmail();
        LeaveRequestResponse response = leaveService.applyLeave(request, currentUserEmail);
        return ResponseEntity.ok(ResponseWrapper.success("Leave applied successfully", response));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ResponseWrapper<LeaveRequestResponse>> approveLeave(@PathVariable Long id,
            @Valid @RequestBody LeaveApproveRejectRequest request) {
        LeaveRequestResponse response = leaveService.approveLeave(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Leave approved successfully", response));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ResponseWrapper<LeaveRequestResponse>> rejectLeave(@PathVariable Long id,
            @Valid @RequestBody LeaveApproveRejectRequest request) {
        LeaveRequestResponse response = leaveService.rejectLeave(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Leave rejected successfully", response));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<ResponseWrapper<LeaveBalanceResponse>> getBalance(@PathVariable Long userId) {
        String currentUserEmail = userService.getCurrentUserEmail();
        checkAccess(currentUserEmail, userId);

        LeaveBalanceResponse response = leaveService.getBalance(userId);
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ResponseWrapper<List<LeaveRequestResponse>>> getHistory(@PathVariable Long userId) {
        String currentUserEmail = userService.getCurrentUserEmail();
        checkAccess(currentUserEmail, userId);

        List<LeaveRequestResponse> response = leaveService.getHistory(userId);
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    private void checkAccess(String currentUserEmail, Long targetUserId) {
        UserResponse currentUser = userService.getUserResponseByEmail(currentUserEmail);

        boolean isAdminOrHr = "ADMIN".equals(currentUser.getRoleName()) || "HR".equals(currentUser.getRoleName());
        boolean isSameUser = currentUser.getId().equals(targetUserId);

        if (!isAdminOrHr && !isSameUser) {
            throw new IllegalArgumentException("Access denied");
        }
    }
}
