package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.config.ResponseWrapper;
import com.yourcompany.hrms.dto.HolidayRequest;
import com.yourcompany.hrms.dto.HolidayResponse;
import com.yourcompany.hrms.dto.UserResponse;
import com.yourcompany.hrms.service.HolidayService;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<HolidayResponse>> createHoliday(@Valid @RequestBody HolidayRequest request) {
        String currentUserEmail = userService.getCurrentUserEmail();
        UserResponse user = userService.getUserResponseByEmail(currentUserEmail);
        HolidayResponse response = holidayService.createHoliday(request, user.getOrgId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Holiday created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<HolidayResponse>>> getAllHolidays() {
        String currentUserEmail = userService.getCurrentUserEmail();
        UserResponse user = userService.getUserResponseByEmail(currentUserEmail);
        List<HolidayResponse> holidays = holidayService.getAllHolidays(user.getOrgId());
        return ResponseEntity.ok(ResponseWrapper.success(holidays));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<HolidayResponse>> getHolidayById(@PathVariable Long id) {
        HolidayResponse response = holidayService.getHolidayById(id);
        return ResponseEntity.ok(ResponseWrapper.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<HolidayResponse>> updateHoliday(@PathVariable Long id,
            @Valid @RequestBody HolidayRequest request) {
        HolidayResponse response = holidayService.updateHoliday(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Holiday updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Object>> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(ResponseWrapper.success("Holiday deleted successfully"));
    }
}
