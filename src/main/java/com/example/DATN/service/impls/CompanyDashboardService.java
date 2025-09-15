package com.example.DATN.service.impls;

import com.example.DATN.dto.response.MonthlyLocationSummary;
import com.example.DATN.dto.response.ReviewResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Ad;
import com.example.DATN.entity.Review;
import com.example.DATN.entity.ReviewImage;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.*;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public List<MonthlyLocationSummary> getMonthlyLocationSummary(Integer locationId) {
        List<Object[]> views = locationViewLogRepository.countMonthlyViewsByLocation(locationId);
        List<Object[]> favorites = favoriteRepository.countMonthlyFavoritesByLocation(locationId);
        List<Object[]> directions = directionRequestLogRepository.countMonthlyDirectionsByLocation(locationId);

        Map<String, MonthlyLocationSummary> summaryMap = new HashMap<>();

        // merge views
        for (Object[] row : views) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            Long total = ((Number) row[2]).longValue();
            String key = year + "-" + month;

            summaryMap.putIfAbsent(key, new MonthlyLocationSummary(year, month, 0L, 0L, 0L));
            summaryMap.get(key).setTotalViews(total);
        }

        // merge favorites
        for (Object[] row : favorites) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            Long total = ((Number) row[2]).longValue();
            String key = year + "-" + month;

            summaryMap.putIfAbsent(key, new MonthlyLocationSummary(year, month, 0L, 0L, 0L));
            summaryMap.get(key).setTotalFavorites(total);
        }

        // merge directions
        for (Object[] row : directions) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            Long total = ((Number) row[2]).longValue();
            String key = year + "-" + month;

            summaryMap.putIfAbsent(key, new MonthlyLocationSummary(year, month, 0L, 0L, 0L));
            summaryMap.get(key).setTotalDirections(total);
        }

        // sort theo năm + tháng (mới nhất trước)
        return summaryMap.values().stream()
                .sorted(Comparator.comparing(MonthlyLocationSummary::getYear)
                        .thenComparing(MonthlyLocationSummary::getMonth)
                        .reversed())
                .collect(Collectors.toList());
    }

    // 2. Lấy các review mới nhất cho 1 địa điểm
    public List<ReviewResponse> getLatestReviews(Integer locationId) {
        return reviewRepository.findTop5ByLocation_LocationIdAndStatusOrderByCreatedAtDesc(locationId, AccountStatus.ACTIVE)
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
        res.setAvatar(review.getUser().getAvatar());
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
