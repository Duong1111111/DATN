package com.example.DATN.repository;

import com.example.DATN.entity.LocationViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationViewLogRepository extends JpaRepository<LocationViewLog, Long> {
    Long countByLocation_LocationId(Integer locationId);
}