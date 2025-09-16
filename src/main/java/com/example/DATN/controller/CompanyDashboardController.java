package com.example.DATN.controller;

import com.example.DATN.dto.response.MonthlyLocationSummary;
import com.example.DATN.dto.response.ReviewDashboardResponse;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.service.impls.CompanyDashboardService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company/dashboard")
@PreAuthorize("hasRole('COMPANY')")
public class CompanyDashboardController {

    @Autowired
    private CompanyDashboardService companyDashboardService;

    @GetMapping("/{locationId}/monthly-summary")
    public ResponseEntity<BaseResponse<List<MonthlyLocationSummary>>> getMonthlySummary(
            @PathVariable Integer locationId) {

        List<MonthlyLocationSummary> result = companyDashboardService.getMonthlyLocationSummary(locationId);

        return ResponseEntity.ok(
                BaseResponse.success(
                        SuccessCode.SUCCESSFUL,
                        "Lấy thống kê theo tháng thành công",
                        result
                )
        );}

    @GetMapping("/{locationId}/summary")
    public ResponseEntity<Map<String, Object>> getLocationSummary(@PathVariable Integer locationId) {
        Map<String, Object> summary = companyDashboardService.getLocationSummary(locationId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reviews/{locationId}")
    public ResponseEntity<List<ReviewDashboardResponse>> getLatestReviews(@PathVariable Integer locationId) {
        return ResponseEntity.ok(companyDashboardService.getLatestReviews(locationId));
    }

    @GetMapping("/ad-performance")
    public ResponseEntity<Map<String, Object>> getAdPerformance() {
        return ResponseEntity.ok(companyDashboardService.getAdPerformance());
    }
}
