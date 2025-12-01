package com.yourcompany.hrms.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "break_sessions")
public class BreakSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_session_id", nullable = false)
    @JsonManagedReference
    private AttendanceSession attendanceSession;

    @Column(name = "break_start_time", nullable = false)
    private LocalDateTime breakStartTime;

    @Column(name = "break_start_photo_url")
    private String breakStartPhotoUrl;

    @Column(name = "break_start_lat")
    private Double breakStartLat;

    @Column(name = "break_start_lon")
    private Double breakStartLon;

    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;

    @Column(name = "break_end_photo_url")
    private String breakEndPhotoUrl;

    @Column(name = "break_end_lat")
    private Double breakEndLat;

    @Column(name = "break_end_lon")
    private Double breakEndLon;

    @Override
    public String toString() {
        return "BreakSession{" +
                "id=" + id +
                ", attendanceSession=" + attendanceSession +
                ", breakStartTime=" + breakStartTime +
                ", breakStartPhotoUrl='" + breakStartPhotoUrl + '\'' +
                ", breakStartLat=" + breakStartLat +
                ", breakStartLon=" + breakStartLon +
                ", breakEndTime=" + breakEndTime +
                ", breakEndPhotoUrl='" + breakEndPhotoUrl + '\'' +
                ", breakEndLat=" + breakEndLat +
                ", breakEndLon=" + breakEndLon +
                ", breakDurationMinutes=" + breakDurationMinutes +
                '}';
    }

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;


}
