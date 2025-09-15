package com.example.DATN.service.impls;

import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Ad;
import com.example.DATN.entity.Review;
import com.example.DATN.entity.ReviewImage;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.*;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyDashboardService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationViewLogRepository locationViewLogRepository;

    @Autowired
    private DirectionRequestLogRepository directionRequestLogRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AdActionLogRepository adActionLogRepository;

    // 1. Tổng quan cho 1 địa điểm
    public Map<String, Object> getLocationSummary(Integer locationId) {
        Long totalViews = locationViewLogRepository.countByLocation_LocationId(locationId);
        Long totalFavorites = favoriteRepository.countByLocation_LocationId(locationId);
        Long totalDirections = directionRequestLogRepository.countByLocation_LocationId(locationId);

        return Map.of(
                "totalViews", totalViews,
                "totalFavorites", totalFavorites,
                "totalDirections", totalDirections
        );
    }

    // 2. Lấy các review mới nhất cho 1 địa điểm
    public List<ReviewResponse> getLatestReviews(Integer locationId) {
        return reviewRepository.findTop5ByLocation_LocationIdOrderByCreatedAtDesc(locationId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setReviewId(review.getReviewId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setStatus(review.getStatus());
        res.setUsername(review.getUser().getUsername());
        res.setLocationName(review.getLocation().getName());
        res.setImages(
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList()
        );
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());
        return res;
    }

    // 3. Hiệu suất quảng cáo của công ty
    public Map<String, Object> getAdPerformance() {
        // Lấy user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account currentUser = accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        // Lấy danh sách quảng cáo của công ty
        List<Ad> ads = adActionLogRepository.findAdsByCompanyId(currentUser.getUserId());

        // Tính toán tổng impression / click
        Long totalImpressions = adActionLogRepository.countByActionTypeAndAdIn("IMPRESSION", ads);
        Long totalClicks = adActionLogRepository.countByActionTypeAndAdIn("CLICK", ads);

        return Map.of(
                "totalImpressions", totalImpressions,
                "totalClicks", totalClicks
        );
    }
}
