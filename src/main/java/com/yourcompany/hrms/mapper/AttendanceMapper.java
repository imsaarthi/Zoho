package com.yourcompany.hrms.mapper;

import com.yourcompany.hrms.dto.AttendanceActionResponse;
import com.yourcompany.hrms.dto.AttendanceDaySummaryResponse;
import com.yourcompany.hrms.dto.AttendanceSessionResponse;
import com.yourcompany.hrms.dto.BreakInfoResponse;
import com.yourcompany.hrms.dto.BreakSessionResponse;
import com.yourcompany.hrms.entity.AttendanceDaySummary;
import com.yourcompany.hrms.entity.AttendanceSession;
import com.yourcompany.hrms.entity.AttendanceStatus;
import com.yourcompany.hrms.entity.BreakSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendanceMapper {

    public AttendanceActionResponse toActionResponse(AttendanceSession session, AttendanceStatus status) {
        if (session == null) {
            return null;
        }
        return AttendanceActionResponse.builder()
                .sessionId(session.getId())
                .workDate(session.getWorkDate())
                .checkIn(session.getCheckIn())
                .checkOut(session.getCheckOut())
                .breaks(toBreakInfoList(session.getBreakSessions()))
                .totalWorkMinutes(session.getDurationMinutes())
                .attendanceStatus(status != null ? status.name() : null)
                .photoUrl(session.getCheckOutPhotoUrl() != null ? session.getCheckOutPhotoUrl()
                        : session.getCheckInPhotoUrl())
                .checkInLat(session.getCheckInLat())
                .checkInLon(session.getCheckInLon())

                .build();
    }

    public AttendanceActionResponse toActionResponse(AttendanceSession session, AttendanceStatus status,
            String photoUrl) {
        if (session == null) {
            return null;
        }
        return AttendanceActionResponse.builder()
                .sessionId(session.getId())
                .workDate(session.getWorkDate())
                .checkIn(session.getCheckIn())
                .checkOut(session.getCheckOut())
                .breaks(toBreakInfoList(session.getBreakSessions()))
                .totalWorkMinutes(session.getDurationMinutes())
                .attendanceStatus(status != null ? status.name() : null)
                .photoUrl(photoUrl)
                .checkInLat(session.getCheckInLat())
                .checkInLon(session.getCheckInLon())
                .build();
    }

    public List<BreakInfoResponse> toBreakInfoList(List<BreakSession> breakSessions) {
        if (breakSessions == null) {
            return List.of();
        }
        return breakSessions.stream()
                .map(this::toBreakInfo)
                .collect(Collectors.toList());
    }

    public BreakInfoResponse toBreakInfo(BreakSession breakSession) {
        if (breakSession == null) {
            return null;
        }
        return BreakInfoResponse.builder()
                .breakIn(breakSession.getBreakStartTime())
                .breakOut(breakSession.getBreakEndTime())
                .breakDurationMinutes(breakSession.getBreakDurationMinutes())
                .breakInPhotoUrl(breakSession.getBreakStartPhotoUrl())
                .breakOutPhotoUrl(breakSession.getBreakEndPhotoUrl())
                .build();
    }

    public AttendanceSessionResponse toSessionResponse(AttendanceSession session) {
        if (session == null) {
            return null;
        }
        return AttendanceSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUser().getId())
                .workDate(session.getWorkDate())
                .checkIn(session.getCheckIn())
                .checkOut(session.getCheckOut())
                .durationMinutes(session.getDurationMinutes())
                .checkInPhotoUrl(session.getCheckInPhotoUrl())
                .checkOutPhotoUrl(session.getCheckOutPhotoUrl())
                .breaks(toBreakSessionResponseList(session.getBreakSessions()))
                .checkInLat(session.getCheckInLat())
                .checkInLon(session.getCheckInLon())

                .build();
    }

    public List<AttendanceSessionResponse> toListResponse(List<AttendanceSession> sessions) {
        if (sessions == null) {
            return List.of();
        }
        return sessions.stream()
                .map(this::toSessionResponse)
                .collect(Collectors.toList());
    }

    public AttendanceDaySummaryResponse toDaySummaryResponse(AttendanceDaySummary summary) {
        if (summary == null) {
            return null;
        }
        Double totalWorkMinutes = Double.valueOf(summary.getTotalWorkMinutes());

        return AttendanceDaySummaryResponse.builder()
                .date(summary.getAttendanceDate())
                .firstCheckIn(summary.getFirstCheckIn())
                .lastCheckOut(summary.getLastCheckOut())
                .status(summary.getStatus())
                .longitude(summary.getLongitude())
                .latitude(summary.getLatitude())
                .totalWorkMinutes(summary.getTotalWorkMinutes())
                .totalWorkHours(Math.round((totalWorkMinutes / 60.0) * 100.0) / 100.0)
                .build();
    }

    public List<BreakSessionResponse> toBreakSessionResponseList(List<BreakSession> breakSessions) {
        if (breakSessions == null) {
            return List.of();
        }
        return breakSessions.stream()
                .map(this::toBreakSessionResponse)
                .collect(Collectors.toList());
    }

    public BreakSessionResponse toBreakSessionResponse(BreakSession breakSession) {
        if (breakSession == null) {
            return null;
        }
        return BreakSessionResponse.builder()
                .id(breakSession.getId())
                .breakStartTime(breakSession.getBreakStartTime())
                .breakEndTime(breakSession.getBreakEndTime())
                .breakStartPhotoUrl(breakSession.getBreakStartPhotoUrl())
                .breakEndPhotoUrl(breakSession.getBreakEndPhotoUrl())
                .durationMinutes(breakSession.getBreakDurationMinutes())
                .breakStartLon(breakSession.getBreakStartLon())
                .breakStartLat(breakSession.getBreakStartLat())
                .breakEndLat(breakSession.getBreakEndLat())
                .breakEndLon(breakSession.getBreakStartLon())
                .build();
    }
}
