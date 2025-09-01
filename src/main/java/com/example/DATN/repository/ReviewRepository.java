package com.example.DATN.repository;

import com.example.DATN.entity.Review;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByStatus(AccountStatus status);
    long countByLocation_LocationId(Integer locationId);
    long countByLocation_LocationIdAndCreatedAtBetween(
            Integer locationId,
            LocalDateTime from,
            LocalDateTime to
    );
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.location.locationId = :locationId")
    Double findAverageRatingByLocationId(@Param("locationId") Integer locationId);
    List<Review> findByLocation_LocationId(Integer locationId);
}