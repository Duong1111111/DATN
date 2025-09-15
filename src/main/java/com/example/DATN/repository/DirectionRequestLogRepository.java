package com.example.DATN.repository;

import com.example.DATN.entity.DirectionRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectionRequestLogRepository extends JpaRepository<DirectionRequestLog, Long> {
    Long countByLocation_LocationId(Integer locationId);
    @Query("SELECT EXTRACT(YEAR FROM d.requestTimestamp), EXTRACT(MONTH FROM d.requestTimestamp), COUNT(d) " +
            "FROM DirectionRequestLog d " +
            "WHERE d.location.locationId = :locationId " +
            "GROUP BY EXTRACT(YEAR FROM d.requestTimestamp), EXTRACT(MONTH FROM d.requestTimestamp)")
    List<Object[]> countMonthlyDirectionsByLocation(@Param("locationId") Integer locationId);
}