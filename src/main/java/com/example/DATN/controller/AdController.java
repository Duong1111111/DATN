package com.example.DATN.controller;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;
import com.example.DATN.service.impls.AdActionLogService;
import com.example.DATN.service.interfaces.AdService;
import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    private final AdService adService;
    private final AdActionLogService adActionLogService;

    public AdController(AdService adService, AdActionLogService adActionLogService) {
        this.adService = adService;
        this.adActionLogService = adActionLogService;
    }

    @GetMapping
    public List<AdResponse> getAll() {
        return adService.getAll();
    }
    @GetMapping("/{id}")
    public AdResponse getAdById(@PathVariable Integer id) {
        return adService.getById(id);
    }

    @PostMapping
    public AdResponse create(@RequestBody AdRequest request) {
        return adService.create(request);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<AdResponse> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(adService.approveAd(id));
    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<BaseResponse<AdResponse>> rejectAd(
            @PathVariable Integer id
    ) {
        AdResponse response = adService.rejectAd(id);
        return ResponseEntity.ok(BaseResponse.success(SuccessCode.SUCCESSFUL, response));
    }


    @PutMapping("/{id}")
    public AdResponse update(@PathVariable Integer id, @RequestBody AdRequest request) {
        return adService.update(id, request);
    }
    @GetMapping("/pending")
    public ResponseEntity<List<AdResponse>> getPendingAds() {
        return ResponseEntity.ok(adService.getPendingAds());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adService.delete(id);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AdResponse>> getMyAds() {
        return ResponseEntity.ok(adService.getMyAds());
    }

    @PostMapping("/{adId}/click")
    public ResponseEntity<Void> logAdClick(@PathVariable Integer adId) {
        String username = getCurrentUsername();
        adActionLogService.logClick(adId, username);
        return ResponseEntity.ok().build();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }
}