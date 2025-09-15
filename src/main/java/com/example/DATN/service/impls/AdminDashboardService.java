package com.example.DATN.service.impls;

import com.example.DATN.dto.response.GrowthDTO;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.AdRepository;
import com.example.DATN.repository.LocationRepository;
import com.example.DATN.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final AccountRepository accountRepository;
    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final AdRepository adRepository;

    public Map<String, Object> getSummaryStats() {
        Map<String, Object> summary = new HashMap<>();

        // --- Module 1: Tăng trưởng nền tảng ---
        Map<String, Long> userByRole = new HashMap<>();
        List<Object[]> counts = accountRepository.countUsersByRole();
        for (Object[] row : counts) {
            // row[0] là Role (enum), row[1] là count
            String role = row[0] != null ? row[0].toString() : "UNKNOWN";
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            userByRole.put(role, count);
        }
        summary.put("usersByRole", userByRole);
        summary.put("totalLocations", locationRepository.count());
        summary.put("totalReviews", reviewRepository.count());

        // --- Module 2: Hiệu quả vận hành ---
        Double avgApprovalHours = accountRepository.getAvgCompanyApprovalTime();
        summary.put("avgCompanyApprovalHours", avgApprovalHours != null ? avgApprovalHours : 0);

        // --- Module 3: Tài chính ---
        Double totalRevenue = adRepository.getTotalRevenue();
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);

        return summary;
    }

    /**
     * Trả dữ liệu chart tăng trưởng theo kỳ (day / week / month / year)
     */
    public Map<String, Object> getGrowthChartData(String period) {
        Map<String, Object> result = new HashMap<>();

        List<GrowthDTO> userGrowth = mapGrowth(accountRepository.countUserGrowth(period));
        List<GrowthDTO> locationGrowth = mapGrowth(locationRepository.countLocationGrowth(period));
        List<GrowthDTO> reviewGrowth = mapGrowth(reviewRepository.countReviewGrowth(period));

        result.put("userGrowth", userGrowth);
        result.put("locationGrowth", locationGrowth);
        result.put("reviewGrowth", reviewGrowth);

        return result;
    }

    private List<GrowthDTO> mapGrowth(List<Object[]> raw) {
        return raw.stream()
                .map(r -> new GrowthDTO(
                        r[0] != null ? r[0].toString() : "N/A",
                        r[1] != null ? ((Number) r[1]).longValue() : 0L
                ))
                .collect(Collectors.toList());
    }
}
