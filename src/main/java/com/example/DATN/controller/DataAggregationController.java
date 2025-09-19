package com.example.DATN.controller;

import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.impls.DataAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/data-aggregation")
@RequiredArgsConstructor
public class DataAggregationController {

    private final DataAggregationService dataAggregationService;
    private final AccountRepository accountRepository;

    // Endpoint mới để server Node.js gọi
    @PostMapping("/snapshot")
    public ResponseEntity<String> getPerformanceSnapshot(@RequestBody Map<String, Integer> payload) {
        Integer companyId = payload.get("companyId");
        if (companyId == null) {
            return ResponseEntity.badRequest().body("Thiếu companyId");
        }

        try {
            String snapshotJson = dataAggregationService.getPerformanceSnapshotAsJson(companyId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(snapshotJson);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}