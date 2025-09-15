package com.example.DATN.controller;

import com.example.DATN.service.impls.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ endpoint
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryStats() {
        return ResponseEntity.ok(adminDashboardService.getSummaryStats());
    }

    @GetMapping("/growth-chart")
    public ResponseEntity<?> getGrowthChartData(@RequestParam String period) {
        return ResponseEntity.ok(adminDashboardService.getGrowthChartData(period));
    }
}