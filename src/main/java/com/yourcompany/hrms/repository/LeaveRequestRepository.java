package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.dto.LeaveRequestResponse;
import com.yourcompany.hrms.entity.LeaveRequest;
import com.yourcompany.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId, Sort sort);

    List<LeaveRequest> findByStatus(LeaveStatus status);
}
