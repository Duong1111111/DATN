package com.example.DATN.service.impls;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;
import com.example.DATN.entity.Ad;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.AdRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.service.interfaces.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {
    @Autowired
    private AdRepository adRepo;
    @Autowired private AccountRepository accountRepo;
    @Autowired private LocationRepository locationRepo;

    @Override
    public List<AdResponse> getAll() {
        return adRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public AdResponse create(AdRequest request) {
        Ad ad = new Ad();
        ad.setStartDate(request.getStartDate());
        ad.setEndDate(request.getEndDate());
        ad.setBudget(request.getBudget());
        ad.setStatus(request.getStatus()!= null ? request.getStatus() : true);
        ad.setCreatedAt(LocalDateTime.now());
        ad.setUpdatedAt(LocalDateTime.now());
        ad.setCreatedBy(accountRepo.findById(request.getCreatedById()).orElseThrow());
        ad.setLocation(locationRepo.findById(request.getLocationId()).orElseThrow());
        return toResponse(adRepo.save(ad));
    }

    @Override
    public AdResponse update(Integer id, AdRequest request) {
        Ad ad = adRepo.findById(id).orElseThrow();
        if (request.getStartDate() != null) ad.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) ad.setEndDate(request.getEndDate());
        if (request.getBudget() != null) ad.setBudget(request.getBudget());
        if (request.getStatus() != null) ad.setStatus(request.getStatus());
        if (request.getLocationId() != null) {
            ad.setLocation(locationRepo.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found")));
        }
        ad.setUpdatedAt(LocalDateTime.now());
        return toResponse(adRepo.save(ad));
    }

    @Override
    public void delete(Integer id) {
        adRepo.deleteById(id);
    }
    @Override
    public AdResponse getById(Integer id) {
        Ad ad = adRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found with id: " + id));
        return toResponse(ad);
    }


    private AdResponse toResponse(Ad ad) {
        AdResponse res = new AdResponse();
        res.setAdId(ad.getAdId());
        res.setStartDate(ad.getStartDate());
        res.setEndDate(ad.getEndDate());
        res.setBudget(ad.getBudget());
        res.setStatus(ad.getStatus());
        res.setCreatedAt(ad.getCreatedAt());
        res.setUpdatedAt(ad.getUpdatedAt());
        res.setCreatedByUsername(ad.getCreatedBy().getUsername());
        res.setLocationName(ad.getLocation().getName());
        return res;
    }
}