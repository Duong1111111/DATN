package com.example.DATN.repository;

import com.example.DATN.entity.DirectionRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectionRequestLogRepository extends JpaRepository<DirectionRequestLog, Long> {
    Long countByLocation_LocationId(Integer locationId);
}