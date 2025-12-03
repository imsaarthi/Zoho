package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.BreakSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreakSessionRepository extends JpaRepository<BreakSession,Long> {


    List<BreakSession> findAllByAttendanceSession_IdAndBreakDurationMinutes(Long attendanceSession, Integer breakDurationMinutes);
}
