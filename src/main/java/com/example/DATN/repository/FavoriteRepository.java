package com.example.DATN.repository;

import com.example.DATN.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    long countByLocation_LocationId(Integer locationId);
    long countByLocation_LocationIdAndCreatedAtBetween(
            Integer locationId,
            LocalDateTime from,
            LocalDateTime to
    );
}