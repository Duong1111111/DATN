package com.example.DATN.service.impls;

import com.example.DATN.dto.request.AdRequest;
import com.example.DATN.dto.response.AdResponse;
import com.example.DATN.entity.*;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.*;
import com.example.DATN.service.interfaces.AdService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private ReviewRepository reviewRepository;

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
        ad.setBudget(request.getBudget());
        ad.setStartDate(request.getStartDate());
        ad.setEndDate(request.getEndDate());
        ad.setStatus(AccountStatus.PENDING);
        ad.setLocation(location);
        ad.setCategories(categories);
        ad.setCreatedAt(LocalDateTime.now());
        ad.setUpdatedAt(LocalDateTime.now());
        ad.setCreatedBy(accountRepo.findById(request.getCreatedById()).orElseThrow());

        adRepo.save(ad);
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
        Ad ad = adRepo.findById(id).orElseThrow(() -> new RuntimeException("Ad not found with id: " + id));

        if (request.getStatus() != null) {
            ad.setStatus(request.getStatus());
        }
        // Thêm logic cập nhật PaymentStatus
        if (request.getPaymentStatus() != null) {
            // Giả sử bạn đã thêm trường paymentStatus vào entity Ad
             ad.setPaymentStatus(request.getPaymentStatus());
        }
        if (request.getStartDate() != null) ad.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) ad.setEndDate(request.getEndDate());
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

    @Override
    public List<AdResponse> getMyAds() {
        String username = getCurrentUsername();
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<Ad> ads = adRepo.findByCreatedBy(account);

        return ads.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
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
        res.setTitle(ad.getTitle());
        res.setActions(ad.getActions().stream().map(Enum::name).collect(Collectors.toList()));
        if (ad.getLocation() != null) {
            Location location = ad.getLocation();
            res.setLocationId(location.getLocationId());
            res.setLocationName(location.getName());
            res.setLocationAddress(location.getLocation());
            res.setLocationImages(
                    location.getImages().stream()
                            .map(LocationImage::getImageUrl)
                            .collect(Collectors.toList())
            );
            Double avgRating = reviewRepository.findAverageRatingByLocationId(location.getLocationId());
            res.setAverageRating(avgRating != null ? avgRating : 0.0);
            res.setTotalReviews((int) reviewRepository.countByLocation_LocationId(location.getLocationId()));
        }
        return res;
    }
}