package com.yourcompany.hrms.repository;

import com.yourcompany.hrms.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByOrganizationIdOrderByDateAsc(Long organizationId);

    List<Holiday> findByOrganizationId(Long organizationId);

    boolean existsByDateAndOrganizationId(LocalDate date, Long organizationId);

    List<Holiday> findByOrganizationIdAndDateBetweenOrderByDateAsc(Long organizationId, LocalDate startDate,
            LocalDate endDate);
}
