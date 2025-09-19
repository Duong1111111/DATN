package com.example.DATN.service.impls;

import com.example.DATN.dto.response.MonthlyLocationSummary;
import com.example.DATN.dto.response.ReviewDashboardResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Company;
import com.example.DATN.entity.Location;
import com.example.DATN.repository.*;
import com.example.DATN.utils.enums.options.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataAggregationService {

    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;
    private final AdActionLogRepository adActionLogRepository;
    private final CompanyDashboardService companyDashboardService; // Tái sử dụng logic có sẵn

    /**
     * Thu thập một bản chụp toàn diện về hiệu suất của công ty,
     * bao gồm tất cả các số liệu từ Company Dashboard cho mọi địa điểm.
     *
     * @param companyId ID của tài khoản công ty.
     * @return Một chuỗi JSON chứa toàn bộ dữ liệu tổng hợp.
     */
    @Transactional
    public String getPerformanceSnapshotAsJson(Integer companyId) {
        Account companyAccount = accountRepository.findByUserIdAndRole(companyId, Role.COMPANY)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Map<String, Object> dataSnapshot = new HashMap<>();
        LocalDate today = LocalDate.now();

        // 1. Lấy thông tin cơ bản và hiệu suất quảng cáo tổng thể
        dataSnapshot.put("thong_tin_cong_ty", Map.of("ten_cong_ty", companyAccount.getCompany().getCompanyName()));
        dataSnapshot.put("ngay_tong_hop", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dataSnapshot.put("hieu_suat_quang_cao_tong_the", companyDashboardService.getAdPerformance());

        // 2. Lấy danh sách tất cả các địa điểm của công ty
        List<Location> companyLocations = locationRepository.findLocationsWithAdsByUserId(companyId);

        // 3. Lặp qua từng địa điểm để thu thập dữ liệu chi tiết
        List<Map<String, Object>> locationsData = companyLocations.stream().map(location -> {
            Map<String, Object> singleLocationData = new HashMap<>();
            Integer locationId = location.getLocationId();

            // Lấy dữ liệu từ CompanyDashboardService
            Map<String, Object> summary = companyDashboardService.getLocationSummary(locationId);
            List<MonthlyLocationSummary> monthlySummary = companyDashboardService.getMonthlyLocationSummary(locationId);
            List<ReviewDashboardResponse> reviewsThisMonth = companyDashboardService.getMonthlyReviews(locationId, today.getYear(), today.getMonthValue());

            Map<String, Object> adPerformance = getAdPerformanceByLocation(locationId);

            singleLocationData.put("ten_dia_diem", location.getName());
            singleLocationData.put("id_dia_diem", locationId);
            singleLocationData.put("tong_quan_tuong_tac", summary); // Tổng lượt xem, yêu thích, chỉ đường
            singleLocationData.put("thong_ke_tuong_tac_hang_thang", monthlySummary);
            singleLocationData.put("danh_gia_trong_thang_nay", reviewsThisMonth);
            singleLocationData.put("hieu_suat_quang_cao", adPerformance);

            return singleLocationData;
        }).collect(Collectors.toList());

        dataSnapshot.put("chi_tiet_hieu_suat_tung_dia_diem", locationsData);

        // 4. Tuần tự hóa toàn bộ dữ liệu thành JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Hỗ trợ serialize Java Time
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataSnapshot);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error serializing data snapshot: " + e.getMessage());
            return "{\"error\": \"Không thể tuần tự hóa dữ liệu.\"}";
        }
    }

    public Map<String, Object> getAdPerformanceByLocation(Integer locationId) {
        Map<String, Object> result = new HashMap<>();

        // Ví dụ: lấy số impression, click từ bảng AdActionLog
        Long impressions = adActionLogRepository.countByAd_Location_LocationIdAndActionType(locationId, "IMPRESSION");
        Long clicks = adActionLogRepository.countByAd_Location_LocationIdAndActionType(locationId, "CLICK");

        result.put("impressions", impressions);
        result.put("clicks", clicks);
        return result;
    }

}