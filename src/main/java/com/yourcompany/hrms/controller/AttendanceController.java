package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.dto.AttendanceActionRequest;
import com.yourcompany.hrms.dto.AttendanceActionResponse;
import com.yourcompany.hrms.dto.AttendanceDaySummaryResponse;
import com.yourcompany.hrms.dto.DailyAttendanceSummary;
import com.yourcompany.hrms.service.AttendanceService;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    @PostMapping(value = "/action", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<AttendanceActionResponse>> processAction(
            @Valid @ModelAttribute AttendanceActionRequest request) {
        String currentUserEmail = userService.getCurrentUserEmail();
        AttendanceActionResponse response = attendanceService.processAttendanceAction(currentUserEmail, request);
        return ResponseEntity.ok(ResponseWrapper.success("Action processed successfully", response));
    }

    @GetMapping("/day-summary/{date}")
    public ResponseEntity<ResponseWrapper<AttendanceDaySummaryResponse>> getDaySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String currentUserEmail = userService.getCurrentUserEmail();
        AttendanceDaySummaryResponse response = attendanceService.getDaySummary(currentUserEmail, date);
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    @GetMapping("/today")
    public ResponseEntity<ResponseWrapper<DailyAttendanceSummary>> getTodaySummary() {
        String currentUserEmail = userService.getCurrentUserEmail();
        DailyAttendanceSummary summary = attendanceService.getTodaySummary(currentUserEmail);
        return ResponseEntity.ok(ResponseWrapper.success(summary));

    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<ResponseWrapper<List<DailyAttendanceSummary>>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        String currentUserEmail = userService.getCurrentUserEmail();
        List<DailyAttendanceSummary> history = attendanceService.getUserHistory(currentUserEmail, userId, from, to);
        return ResponseEntity.ok(ResponseWrapper.success(history));
    }
}
