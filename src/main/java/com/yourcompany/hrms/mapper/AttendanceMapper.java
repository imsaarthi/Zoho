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
                        : session.getCheckInPhotoUrl()) // Use check-out photo if available, else check-in. Or maybe I
                                                        // should pass the specific photo URL from the action?
                // The processAttendanceAction returns the session, but the specific photo URL
                // for the action is what we want.
                // However, the session has the latest photo URL for the action performed.
                // If check-in, checkInPhotoUrl is set. If check-out, checkOutPhotoUrl is set.
                // If break-in/out, break session has it.
                // Let's rely on the service to set the correct photoUrl in the response, or we
                // can infer it here.
                // Actually, the service returns AttendanceActionResponse, so the service should
                // build it or the mapper should be smart enough.
                // But the mapper takes the session.
                // Let's look at processAttendanceAction in Service. It calls
                // mapper.toActionResponse(session, status).
                // It doesn't pass the specific photo URL.
                // I should probably update toActionResponse to accept photoUrl or logic to pick
                // it
                // For now, I'll set it to null here and let the service set it, or update the
                // mapper signature.
                // Updating mapper signature is better.
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
                // .userId(session.getUser().getId()) // Removed as per DTO check (if not
                // present)
                // .userFullName(session.getUser().getFullName()) // Removed as per DTO check
                .workDate(session.getWorkDate())
                .checkIn(session.getCheckIn())
                .checkOut(session.getCheckOut())
                .durationMinutes(session.getDurationMinutes())
                .checkInPhotoUrl(session.getCheckInPhotoUrl())
                .checkOutPhotoUrl(session.getCheckOutPhotoUrl())
                .breaks(toBreakSessionResponseList(session.getBreakSessions()))
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
        return AttendanceDaySummaryResponse.builder()
                .date(summary.getAttendanceDate())
                .status(summary.getStatus())
                .totalWorkMinutes(summary.getTotalWorkMinutes())
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
                .durationMinutes(breakSession.getBreakDurationMinutes())
                .build();
    }
}
