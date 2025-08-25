package com.example.DATN.service.impls;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;
import com.example.DATN.entity.Ad;
import com.example.DATN.entity.Category;
import com.example.DATN.entity.Location;
import com.example.DATN.repository.*;
import com.example.DATN.service.interfaces.AdService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.PaymentStatus;
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
    @Autowired private CategoryRepository categoryRepository;
    private final TimeAgoUtil timeAgoUtil;

    public AdServiceImpl(TimeAgoUtil timeAgoUtil) {
        this.timeAgoUtil = timeAgoUtil;
    }

    @Override
    public List<AdResponse> getAll() {
        return adRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public AdResponse create(AdRequest request) {
        Location location = locationRepo.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        Ad ad = new Ad();
        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setActions(request.getActions());
        ad.setBudget(1_500_000d);
        ad.setStartDate(request.getStartDate());
        ad.setEndDate(request.getEndDate());
        ad.setStatus(AccountStatus.PENDING);
        ad.setLocation(location);
        ad.setCategories(categories);
        ad.setCreatedAt(LocalDateTime.now());
        ad.setUpdatedAt(LocalDateTime.now());
        ad.setCreatedBy(accountRepo.findById(request.getCreatedById()).orElseThrow());

        adRepo.save(ad);
//        timeAgoUtil.notifyAdCreated(ad);
        AdResponse res = new AdResponse();
        res.setAdId(ad.getAdId());
        res.setTitle(ad.getTitle());
        res.setDescription(ad.getDescription());
        res.setActions(ad.getActions().stream()
                .map(Enum::name)
                .toList());
        res.setLocationName(location.getName());
        res.setCategories(categories.stream().map(Category::getName).toList());
        res.setStartDate(ad.getStartDate());
        res.setEndDate(ad.getEndDate());
        res.setCreatedByUsername(ad.getCreatedBy().getUsername());
        res.setBudget(ad.getBudget());
        res.setStatus(ad.getStatus());
        res.setCreatedAt(ad.getCreatedAt());
        res.setUpdatedAt(ad.getUpdatedAt());

        return res;
    }


    @Override
    public AdResponse approveAd(Integer adId) {
        Ad ad = adRepo.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        if (ad.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only PENDING ads can be approved.");
        }

        ad.setStatus(AccountStatus.ACTIVE);
        ad.setUpdatedAt(LocalDateTime.now());

        return toResponse(adRepo.save(ad));
    }
    @Override
    public AdResponse rejectAd(Integer adId) {
        Ad ad = adRepo.findById(adId)
                .orElseThrow(() -> new RuntimeException("Ad not found"));

        if (ad.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Only PENDING ads can be rejected.");
        }

        ad.setStatus(AccountStatus.INACTIVE);
        ad.setUpdatedAt(LocalDateTime.now());

        return toResponse(adRepo.save(ad));
    }

    @Override
    public List<AdResponse> getPendingAds() {
        return adRepo.findAllByStatus(AccountStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdResponse update(Integer id, AdRequest request) {
        Ad ad = adRepo.findById(id).orElseThrow();
        if (request.getStartDate() != null) ad.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) ad.setEndDate(request.getEndDate());
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