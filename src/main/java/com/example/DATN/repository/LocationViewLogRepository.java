package com.example.DATN.repository;

import com.example.DATN.entity.LocationViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationViewLogRepository extends JpaRepository<LocationViewLog, Long> {
    Long countByLocation_LocationId(Integer locationId);
    @Query("SELECT EXTRACT(YEAR FROM l.viewTimestamp) as year, " +
            "EXTRACT(MONTH FROM l.viewTimestamp) as month, " +
            "COUNT(l) " +
            "FROM LocationViewLog l " +
            "WHERE l.location.locationId = :locationId " +
            "GROUP BY EXTRACT(YEAR FROM l.viewTimestamp), EXTRACT(MONTH FROM l.viewTimestamp)")
    List<Object[]> countMonthlyViewsByLocation(@Param("locationId") Integer locationId);

    @Query("SELECT COUNT(l) FROM LocationViewLog l WHERE l.location.createdBy.userId = :companyId AND l.viewTimestamp BETWEEN :start AND :end")
    long countViewsByCompanyBetween(@Param("companyId") Integer companyId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}