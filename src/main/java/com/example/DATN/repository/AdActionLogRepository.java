package com.example.DATN.repository;

import com.example.DATN.entity.Ad;
import com.example.DATN.entity.AdActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdActionLogRepository extends JpaRepository<AdActionLog, Long> {

    Long countByActionTypeAndAdIn(String actionType, List<Ad> ads);

    @Query("SELECT a FROM Ad a WHERE a.createdBy.userId = :companyId")
    List<Ad> findAdsByCompanyId(@Param("companyId") Integer companyId);

    Long countByAd_Location_LocationIdAndActionType(Integer locationId, String actionType);
    Long countByAd_CreatedBy_UserIdAndActionType(Integer companyId, String actionType);

    Long countByAd_CreatedBy_Company_UserIdAndActionTypeAndActionTimestampBetween(
            Integer companyId,
            String actionType,
            LocalDateTime start,
            LocalDateTime end
    );
}