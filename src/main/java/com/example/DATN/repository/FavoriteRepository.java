package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.Favorite;
import com.example.DATN.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    long countByLocation_LocationId(Integer locationId);
    long countByLocation_LocationIdAndCreatedAtBetween(
            Integer locationId,
            LocalDateTime from,
            LocalDateTime to
    );
    Optional<Favorite> findByUserAndLocation(Account user, Location location);
    List<Favorite> findByUser(Account user);

}