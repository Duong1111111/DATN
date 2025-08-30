package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;

import java.util.List;

public interface AdService {
    List<AdResponse> getAll();
    AdResponse create(AdRequest request);

    AdResponse approveAd(Integer adId);

    AdResponse rejectAd(Integer adId);

    List<AdResponse> getPendingAds();

    AdResponse update(Integer id, AdRequest request);
    void delete(Integer id);

    AdResponse getById(Integer id);

    List<AdResponse> getMyAds();
}