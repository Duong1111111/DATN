package com.example.DATN.repository;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.Favorite;
import com.example.DATN.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt), COUNT(f) " +
            "FROM Favorite f " +
            "WHERE f.location.locationId = :locationId " +
            "GROUP BY EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt)")
    List<Object[]> countMonthlyFavoritesByLocation(@Param("locationId") Integer locationId);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.location.createdBy.userId = :companyId AND f.createdAt BETWEEN :start AND :end")
    long countFavoritesByCompanyBetween(@Param("companyId") Integer companyId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}