package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    List<AttendanceSession> findByUserIdAndWorkDateOrderByCheckInAsc(Long userId, LocalDate workDate);

    Optional<AttendanceSession> findTopByUserIdAndWorkDateAndCheckOutIsNullOrderByCheckInDesc(Long userId,
            LocalDate workDate);

    List<AttendanceSession> findByUserIdAndWorkDateBetweenOrderByWorkDateAscCheckInAsc(Long userId, LocalDate startDate,
            LocalDate endDate);
}
