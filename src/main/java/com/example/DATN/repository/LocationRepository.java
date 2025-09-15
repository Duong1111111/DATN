package com.example.DATN.repository;

import com.example.DATN.entity.Location;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findAllByStatus(AccountStatus status);
    Optional<Location> findByLocationIdAndStatus(Integer locationId, AccountStatus status);
    @Query("SELECT l FROM Location l WHERE l.createdBy.id = :userId")
    List<Location> findLocationsByUserId(@Param("userId") Integer userId);
    @Query("SELECT l FROM Location l " +
            "WHERE l.createdBy.userId = :userId " +
            "AND l.id NOT IN (SELECT a.location.id FROM Ad a WHERE a.location IS NOT NULL)")
    List<Location> findLocationsNotAdvertised(@Param("userId") Integer userId);

    @Query(value = """
        SELECT DATE_TRUNC(:period, created_at) as period, COUNT(*)
        FROM Location
        GROUP BY period
        ORDER BY period
        """, nativeQuery = true)
    List<Object[]> countLocationGrowth(@Param("period") String period);

}