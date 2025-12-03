package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.AttendanceDaySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDaySummaryRepository extends JpaRepository<AttendanceDaySummary, Long> {
    Optional<AttendanceDaySummary> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);


    List<AttendanceDaySummary> findAllByUserIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(Long id, LocalDate from, LocalDate to);
}
