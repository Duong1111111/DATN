package com.example.DATN.controller;

import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.service.impls.CompanyDashboardService;
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

    @GetMapping("/summary/{locationId}")
    public ResponseEntity<Map<String, Object>> getLocationSummary(@PathVariable Integer locationId) {
        return ResponseEntity.ok(companyDashboardService.getLocationSummary(locationId));
    }

    @GetMapping("/reviews/{locationId}")
    public ResponseEntity<List<ReviewResponse>> getLatestReviews(@PathVariable Integer locationId) {
        return ResponseEntity.ok(companyDashboardService.getLatestReviews(locationId));
    }

    @GetMapping("/ad-performance")
    public ResponseEntity<Map<String, Object>> getAdPerformance() {
        return ResponseEntity.ok(companyDashboardService.getAdPerformance());
    }
}
