package com.example.DATN.controller;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;
import com.example.DATN.service.interfaces.AdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
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
}