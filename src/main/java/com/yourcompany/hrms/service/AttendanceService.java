package com.yourcompany.hrms.service;

import com.yourcompany.hrms.dto.AttendanceActionRequest;
import com.yourcompany.hrms.dto.AttendanceActionResponse;
import com.yourcompany.hrms.dto.AttendanceDaySummaryResponse;
import com.yourcompany.hrms.dto.DailyAttendanceSummary;
import com.yourcompany.hrms.entity.AttendanceDaySummary;
import com.yourcompany.hrms.entity.AttendanceSession;
import com.yourcompany.hrms.entity.AttendanceStatus;
import com.yourcompany.hrms.entity.BreakSession;
import com.yourcompany.hrms.entity.RoleName;
import com.yourcompany.hrms.entity.user.User;
import com.yourcompany.hrms.exception.ResourceNotFoundException;
import com.yourcompany.hrms.mapper.AttendanceMapper;
import com.yourcompany.hrms.repository.AttendanceDaySummaryRepository;
import com.yourcompany.hrms.repository.AttendanceSessionRepository;
import com.yourcompany.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceDaySummaryRepository attendanceDaySummaryRepository;
    private final UserRepository userRepository;
    private final AttendanceMapper attendanceMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public AttendanceActionResponse processAttendanceAction(String currentUserEmail, AttendanceActionRequest request) {
        User user = getUserByEmail(currentUserEmail);

        // Save photo
        String photoUrl = fileStorageService.saveAttendanceImage(request.getFile(), user.getId());

        verifyFace(user.getProfileImageUrl(), photoUrl);

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        AttendanceSession session;
        AttendanceStatus status = null;

        switch (request.getAttendanceType()) {
            case CHECK_IN:
                session = handleCheckIn(user, request, now, today, photoUrl);
                break;
            case CHECK_OUT:
                session = handleCheckOut(user, request, now, today, photoUrl);
                status = updateDailySummary(user, today);
                break;
            case BREAK_IN:
                session = handleBreakIn(user, request, now, today, photoUrl);
                break;
            case BREAK_OUT:
                session = handleBreakOut(user, request, now, today, photoUrl);
                break;
            default:
                throw new IllegalArgumentException("Invalid attendance type");
        }

        return attendanceMapper.toActionResponse(session, status, photoUrl);
    }

    private AttendanceSession handleCheckIn(User user, AttendanceActionRequest request, LocalDateTime now,
            LocalDate today, String photoUrl) {
        // Check if there is already an open session
        attendanceSessionRepository.findTopByUserIdAndWorkDateAndCheckOutIsNullOrderByCheckInDesc(user.getId(), today)
                .ifPresent(s -> {
                    throw new IllegalArgumentException("You are already checked in. Please check out first.");
                });

        AttendanceSession session = AttendanceSession.builder()
                .user(user)
                .organization(user.getOrganization())
                .workDate(today)
                .checkIn(now)
                .checkInLat(request.getLatitude())
                .checkInLon(request.getLongitude())
                .checkInPhotoUrl(photoUrl)
                .breakSessions(new ArrayList<>())
                .build();

        return attendanceSessionRepository.save(session);
    }

    private AttendanceSession handleCheckOut(User user, AttendanceActionRequest request, LocalDateTime now,
            LocalDate today, String photoUrl) {
        AttendanceSession session = getOpenSession(user, today);

        if (now.isBefore(session.getCheckIn())) {
            throw new IllegalArgumentException("Check-out time cannot be before check-in time");
        }

        // Ensure all breaks are closed
        if (session.getBreakSessions() != null) {
            boolean hasOpenBreak = session.getBreakSessions().stream().anyMatch(b -> b.getBreakEndTime() == null);
            if (hasOpenBreak) {
                throw new IllegalArgumentException("Cannot check out while on a break. End the break first.");
            }
        }

        session.setCheckOut(now);
        session.setCheckOutLat(request.getLatitude());
        session.setCheckOutLon(request.getLongitude());
        session.setCheckOutPhotoUrl(photoUrl);

        long minutes = Duration.between(session.getCheckIn(), now).toMinutes();
        session.setDurationMinutes((int) minutes);

        return attendanceSessionRepository.save(session);
    }

    private AttendanceSession handleBreakIn(User user, AttendanceActionRequest request, LocalDateTime now,
            LocalDate today, String photoUrl) {
        AttendanceSession session = getOpenSession(user, today);

        // Check if already on a break
        if (session.getBreakSessions() != null
                && session.getBreakSessions().stream().anyMatch(b -> b.getBreakEndTime() == null)) {
            throw new IllegalArgumentException("Already on a break.");
        }

        BreakSession breakSession = BreakSession.builder()
                .attendanceSession(session)
                .breakStartTime(now)
                .breakStartLat(request.getLatitude())
                .breakStartLon(request.getLongitude())
                .breakStartPhotoUrl(photoUrl)
                .build();

        if (session.getBreakSessions() == null) {
            session.setBreakSessions(new ArrayList<>());
        }
        session.getBreakSessions().add(breakSession);

        return attendanceSessionRepository.save(session);
    }

    private AttendanceSession handleBreakOut(User user, AttendanceActionRequest request, LocalDateTime now,
            LocalDate today, String photoUrl) {
        AttendanceSession session = getOpenSession(user, today);

        BreakSession currentBreak = null;
        if (session.getBreakSessions() != null) {
            currentBreak = session.getBreakSessions().stream()
                    .filter(b -> b.getBreakEndTime() == null)
                    .findFirst()
                    .orElse(null);
        }

        if (currentBreak == null) {
            throw new IllegalArgumentException("No active break found to end.");
        }

        currentBreak.setBreakEndTime(now);
        currentBreak.setBreakEndLat(request.getLatitude());
        currentBreak.setBreakEndLon(request.getLongitude());
        currentBreak.setBreakEndPhotoUrl(photoUrl);

        long minutes = Duration.between(currentBreak.getBreakStartTime(), now).toMinutes();
        currentBreak.setBreakDurationMinutes((int) minutes);

        return attendanceSessionRepository.save(session);
    }

    private AttendanceSession getOpenSession(User user, LocalDate today) {
        return attendanceSessionRepository
                .findTopByUserIdAndWorkDateAndCheckOutIsNullOrderByCheckInDesc(user.getId(), today)
                .orElseThrow(() -> new IllegalArgumentException("No open session found for today."));
    }

    private AttendanceStatus updateDailySummary(User user, LocalDate date) {
        List<AttendanceSession> sessions = attendanceSessionRepository
                .findByUserIdAndWorkDateOrderByCheckInAsc(user.getId(), date);

        if (sessions.isEmpty())
            return AttendanceStatus.ABSENT;

        LocalDateTime firstCheckIn = sessions.get(0).getCheckIn();
        // Last check out is the check out of the last session.
        // Since we just checked out, the last session should have a check out time.
        AttendanceSession lastSession = sessions.get(sessions.size() - 1);
        LocalDateTime lastCheckOut = lastSession.getCheckOut();

        long totalMinutes = sessions.stream()
                .filter(s -> s.getDurationMinutes() != null)
                .mapToLong(AttendanceSession::getDurationMinutes)
                .sum();

        // Deduct fixed 30 mins break
        long effectiveMinutes = Math.max(0, totalMinutes - 30);

        AttendanceStatus status = calculateStatus(firstCheckIn, lastCheckOut, effectiveMinutes);

        AttendanceDaySummary summary = attendanceDaySummaryRepository
                .findByUserIdAndAttendanceDate(user.getId(), date)
                .orElse(AttendanceDaySummary.builder()
                        .user(user)
                        .attendanceDate(date)
                        .build());

        summary.setFirstCheckIn(firstCheckIn);
        summary.setLastCheckOut(lastCheckOut);
        summary.setTotalWorkMinutes(effectiveMinutes);
        summary.setStatus(status);

        attendanceDaySummaryRepository.save(summary);
        return status;
    }

    private AttendanceStatus calculateStatus(LocalDateTime firstCheckIn, LocalDateTime lastCheckOut,
            long effectiveMinutes) {

        if (firstCheckIn == null)
            return AttendanceStatus.ABSENT;

        // Rule 1: Late Check-in > 12:30 PM
        if (firstCheckIn.toLocalTime().isAfter(LocalTime.of(12, 30))) {
            return AttendanceStatus.HALF_DAY;
        }

        // Rule 2: Early Check-out < 8:30 PM (20:30)
        if (lastCheckOut != null && lastCheckOut.toLocalTime().isBefore(LocalTime.of(20, 30))) {
            return AttendanceStatus.HALF_DAY;
        }

        // Rule 3: Total Work Hours < 7.5 hours (450 minutes)
        if (effectiveMinutes < 450) {
            return AttendanceStatus.HALF_DAY;
        }

        return AttendanceStatus.FULL_DAY;
    }

    @Transactional(readOnly = true)
    public AttendanceDaySummaryResponse getDaySummary(String currentUserEmail, LocalDate date) {
        User user = getUserByEmail(currentUserEmail);
        AttendanceDaySummary summary = attendanceDaySummaryRepository
                .findByUserIdAndAttendanceDate(user.getId(), date)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceDaySummary", "date", date));

        return attendanceMapper.toDaySummaryResponse(summary);
    }

    @Transactional(readOnly = true)
    public DailyAttendanceSummary getTodaySummary(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        LocalDate today = LocalDate.now();
        return getDailySummary(user, today);
    }

    @Transactional(readOnly = true)
    public List<DailyAttendanceSummary> getUserHistory(String currentUserEmail, Long userId, LocalDate from,
            LocalDate to) {
        User currentUser = getUserByEmail(currentUserEmail);

        // Authorization check
        boolean isAdminOrHr = currentUser.getRole().getName() == RoleName.ADMIN
                || currentUser.getRole().getName() == RoleName.HR;
        boolean isSameUser = currentUser.getId().equals(userId);

        if (!isAdminOrHr && !isSameUser) {
            throw new IllegalArgumentException("Access denied: You can only view your own attendance history");
        }

        List<AttendanceSession> sessions = attendanceSessionRepository
                .findByUserIdAndWorkDateBetweenOrderByWorkDateAscCheckInAsc(userId, from, to);

        Map<LocalDate, List<AttendanceSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(AttendanceSession::getWorkDate));

        return sessionsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> calculateDailySummary(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private DailyAttendanceSummary getDailySummary(User user, LocalDate date) {
        List<AttendanceSession> sessions = attendanceSessionRepository
                .findByUserIdAndWorkDateOrderByCheckInAsc(user.getId(), date);
        return calculateDailySummary(date, sessions);
    }

    private DailyAttendanceSummary calculateDailySummary(LocalDate date, List<AttendanceSession> sessions) {
        long totalMinutes = sessions.stream()
                .filter(s -> s.getDurationMinutes() != null)
                .mapToLong(AttendanceSession::getDurationMinutes)
                .sum();

//        Long userId = sessions.isEmpty() ? null : sessions.get(0).getUser().getId();
////        List<Long> userIds = sessions.stream()
////                .map(s -> s.getUser().getId())
////                .toList();
        Long userId = sessions.stream()
                .map(s -> s.getUser().getId())
                .findFirst()
                .orElse(null);



        return DailyAttendanceSummary.builder()
                .date(date)
                .totalMinutes(totalMinutes)
                .totalHours(Math.round((totalMinutes / 60.0) * 100.0) / 100.0)
                .sessions(attendanceMapper.toListResponse(sessions))
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private boolean verifyFace(String profileImageUrl, String actionImageUrl) {
        // Mock implementation: always returns true
        log.info("Verifying face: profile={} vs action={}", profileImageUrl, actionImageUrl);
        return true;
    }


    @Transactional(readOnly = true)
    public List<DailyAttendanceSummary> getAllUserhistory(String currentUserEmail, LocalDate from,
                                                       LocalDate to) {
        User currentUser = getUserByEmail(currentUserEmail);
        // Authorization check
        boolean isAdminOrHr = currentUser.getRole().getName() == RoleName.ADMIN
                || currentUser.getRole().getName() == RoleName.HR;

        if (!isAdminOrHr) {
            throw new IllegalArgumentException("Access denied: You can only view your own attendance history");
        }

        List<AttendanceSession> sessions = attendanceSessionRepository
                .findByWorkDateBetweenOrderByWorkDateAscCheckInAsc(from, to);

        Map<LocalDate, List<AttendanceSession>> sessionsByDate = sessions.stream()
                .collect(Collectors.groupingBy(AttendanceSession::getWorkDate));

        return sessionsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> calculateDailySummary(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
