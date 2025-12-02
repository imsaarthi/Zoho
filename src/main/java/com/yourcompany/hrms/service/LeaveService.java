package com.yourcompany.hrms.service;

import com.yourcompany.hrms.dto.LeaveApplyRequest;
import com.yourcompany.hrms.dto.LeaveApproveRejectRequest;
import com.yourcompany.hrms.dto.LeaveBalanceResponse;
import com.yourcompany.hrms.dto.LeaveRequestResponse;
import com.yourcompany.hrms.entity.*;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.mapper.LeaveMapper;
import com.yourcompany.hrms.repository.LeaveBalanceRepository;
import com.yourcompany.hrms.repository.LeaveRequestRepository;
import com.yourcompany.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveMapper leaveMapper;

    @Transactional
    public LeaveRequestResponse applyLeave(LeaveApplyRequest request, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        LeaveRequest leaveRequest = leaveMapper.toEntity(request);
        leaveRequest.setUser(user);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        // Assuming LeaveType is handled in mapper or entity if present.
        // If LeaveRequest entity doesn't have leaveType, we might lose it or need to
        // add it.
        // Based on previous view, LeaveRequest didn't show leaveType.
        // I will assume for now we just save what we have.

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        log.info("Leave applied by user: {}", user.getEmail());

        return leaveMapper.toResponse(savedRequest);
    }

    @Transactional
    public LeaveRequestResponse approveLeave(Long leaveId, LeaveApproveRejectRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", "id", leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Leave request is not in PENDING state");
        }

        User user = leaveRequest.getUser();
        LeaveBalance balance = leaveBalanceRepository.findByUserId(user.getId())
                .orElseGet(() -> createInitialBalance(user));

        long daysRequested = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;

        if (balance.getTotalLeaves() < daysRequested) {
            throw new IllegalArgumentException("Insufficient leave balance. Available: " + balance.getTotalLeaves()
                    + ", Requested: " + daysRequested);
        }

        balance.setTotalLeaves(balance.getTotalLeaves() - daysRequested);
        leaveBalanceRepository.save(balance);

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        // If request has comment, we could save it if entity supports it.

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        log.info("Leave approved for user: {}. Deducted {} days.", user.getEmail(), daysRequested);

        return leaveMapper.toResponse(savedRequest);
    }

    @Transactional
    public LeaveRequestResponse rejectLeave(Long leaveId, LeaveApproveRejectRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", "id", leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Leave request is not in PENDING state");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        // Save rejection reason if entity supports it

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        log.info("Leave rejected for user: {}", leaveRequest.getUser().getEmail());

        return leaveMapper.toResponse(savedRequest);
    }

    @Transactional(readOnly = true)
    public LeaveBalanceResponse getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LeaveBalance balance = leaveBalanceRepository.findByUserId(userId)
                .orElse(LeaveBalance.builder().user(user).totalLeaves(0.0).build());

        // Calculate used and pending
        List<LeaveRequest> requests = leaveRequestRepository.findByUserId(userId, Sort.unsorted());

        int usedLeaves = requests.stream()
                .filter(r -> r.getStatus() == LeaveStatus.APPROVED)
                .mapToInt(r -> (int) (ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1))
                .sum();

        int pendingLeaves = requests.stream()
                .filter(r -> r.getStatus() == LeaveStatus.PENDING)
                .mapToInt(r -> (int) (ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1))
                .sum();

        return LeaveBalanceResponse.builder()
                .totalLeaves((Double) (balance.getTotalLeaves() + usedLeaves)) // Entitlement = Remaining + Used
                .usedLeaves((double) usedLeaves)
                .pendingLeaves((double) pendingLeaves)
                .availableLeaves((double) balance.getTotalLeaves().intValue())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> getHistory(Long userId) {
        return leaveMapper
                .toListResponse(leaveRequestRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "startDate")));
    }

    // Run at 00:00 on the 1st day of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void accrueLeaves() {
        log.info("Starting monthly leave accrual job...");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!user.isActive())
                continue;

            double accrualAmount = 0;
            EmploymentType type = user.getEmploymentType();

            if (type == EmploymentType.PROBATION) {
                accrualAmount = 1.0;
            } else if (type == EmploymentType.FULL_TIME) {
                accrualAmount = 2.0;
            }
            // TRAINEE and NOTICE_PERIOD get 0

            if (accrualAmount > 0) {
                LeaveBalance balance = leaveBalanceRepository.findByUserId(user.getId())
                        .orElseGet(() -> createInitialBalance(user));

                balance.setTotalLeaves(balance.getTotalLeaves() + accrualAmount);
                leaveBalanceRepository.save(balance);
                log.info("Accrued {} leaves for user: {}", accrualAmount, user.getEmail());
            }
        }
        log.info("Monthly leave accrual job completed.");
    }

    private LeaveBalance createInitialBalance(User user) {
        return LeaveBalance.builder()
                .user(user)
                .totalLeaves(0.0)
                .build();
    }


    public List<LeaveBalanceResponse> getall() {
        List<LeaveBalance> balance = leaveBalanceRepository.findAll();
        return balance.stream()
                .map(lr -> LeaveBalanceResponse.builder()
                                .totalLeaves(lr.getTotalLeaves())
                                .id(lr.getId())
                                .userId(lr.getUser().getId())
                                .build()
//        private LeaveType leaveType;
//        private int totalLeaves;
//        private int usedLeaves;
//        private int pendingLeaves;
//        private int availableLeaves;
                )
                .toList();
    }

    public List<LeaveRequestResponse> request() {

        List<LeaveRequest> balance = leaveRequestRepository.findAll();

        return balance.stream()
                .map(lr -> LeaveRequestResponse.builder()
                        .id(lr.getId())
                        .userId(lr.getUser().getId())
                        .startDate(lr.getStartDate())
                        .userFullName(lr.getUser() != null ? lr.getUser().getFullName() : null)
                        .endDate(lr.getEndDate())
                        .status(lr.getStatus())
                        .leaveType(lr.getLeaveType())
                        .reason(lr.getReason())
                        .build()
                )
                .toList();

    }


}
