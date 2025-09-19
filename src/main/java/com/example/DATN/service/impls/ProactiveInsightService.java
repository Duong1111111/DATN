package com.example.DATN.service.impls;

import com.example.DATN.entity.Account;
import com.example.DATN.entity.ProactiveInsight;
import com.example.DATN.repository.*;
import com.example.DATN.utils.enums.options.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProactiveInsightService {

    private static final Logger log = LoggerFactory.getLogger(ProactiveInsightService.class);
    private final AccountRepository accountRepository;
    private final ProactiveInsightRepository insightRepository;
    private final LocationViewLogRepository viewLogRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 2 * * SUN")
//    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void generateWeeklySummaryReport() {
        log.info("Bắt đầu tác vụ tạo báo cáo tổng kết hiệu suất hàng tuần...");
        List<Account> companies = accountRepository.findByRole(Role.COMPANY);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime lastWeekStart = thisWeekStart.minusWeeks(1);
        LocalDateTime lastWeekEnd = thisWeekStart.minusNanos(1);

        for (Account company : companies) {
            try {
                analyzeAndCreateReportForCompany(company, thisWeekStart, now, lastWeekStart, lastWeekEnd);
            } catch (Exception e) {
                log.error("Lỗi khi phân tích cho công ty ID {}: {}", company.getUserId(), e.getMessage());
            }
        }
        log.info("Hoàn thành tác vụ tạo báo cáo.");
    }

    private void analyzeAndCreateReportForCompany(Account company, LocalDateTime thisWeekStart, LocalDateTime thisWeekEnd, LocalDateTime lastWeekStart, LocalDateTime lastWeekEnd) {
        long viewsThisWeek = viewLogRepository.countViewsByCompanyBetween(company.getUserId(), thisWeekStart, thisWeekEnd);
        long viewsLastWeek = viewLogRepository.countViewsByCompanyBetween(company.getUserId(), lastWeekStart, lastWeekEnd);

        long favoritesThisWeek = favoriteRepository.countFavoritesByCompanyBetween(company.getUserId(), thisWeekStart, thisWeekEnd);
        long favoritesLastWeek = favoriteRepository.countFavoritesByCompanyBetween(company.getUserId(), lastWeekStart, lastWeekEnd);

        long reviewsThisWeek = reviewRepository.countReviewsByCompanyBetween(company.getUserId(), thisWeekStart, thisWeekEnd);
        long reviewsLastWeek = reviewRepository.countReviewsByCompanyBetween(company.getUserId(), lastWeekStart, lastWeekEnd);

        StringBuilder summary = new StringBuilder();
        summary.append("### Báo cáo hiệu suất tuần\n\n");
        summary.append("Dưới đây là tổng kết các chỉ số quan trọng của bạn trong tuần này so với tuần trước:\n\n");

        boolean hasSignificantChange = false;

        summary.append(formatMetric("Lượt xem hồ sơ", viewsThisWeek, viewsLastWeek));
        if (isSignificantChange(viewsThisWeek, viewsLastWeek)) hasSignificantChange = true;

        summary.append(formatMetric("Lượt thêm vào yêu thích", favoritesThisWeek, favoritesLastWeek));
        if (isSignificantChange(favoritesThisWeek, favoritesLastWeek)) hasSignificantChange = true;

        summary.append(formatMetric("Đánh giá mới", reviewsThisWeek, reviewsLastWeek));
        if (isSignificantChange(reviewsThisWeek, reviewsLastWeek)) hasSignificantChange = true;

        if (hasSignificantChange) {
            ProactiveInsight insight = new ProactiveInsight();
            insight.setCompany(company);
            insight.setTitle("Báo cáo hiệu suất hàng tuần");
            insight.setSummary(summary.toString());

            Map<String, Object> chartConfig = generateComparisonChart(
                    viewsThisWeek, viewsLastWeek,
                    favoritesThisWeek, favoritesLastWeek,
                    reviewsThisWeek, reviewsLastWeek
            );
            try {
                insight.setDetailsJson(objectMapper.writeValueAsString(chartConfig));
            } catch (JsonProcessingException e) {
                log.error("Lỗi khi tạo JSON biểu đồ cho công ty ID {}: {}", company.getUserId(), e.getMessage());
            }

            insightRepository.save(insight);
            log.info("Đã tạo báo cáo hàng tuần cho công ty: {}", company.getUsername());
        } else {
            log.info("Không có thay đổi đáng kể cho công ty {}, bỏ qua tạo báo cáo.", company.getUsername());
        }
    }

    private String formatMetric(String metricName, long thisWeek, long lastWeek) {
        double change = 0;
        if (lastWeek > 0) {
            change = ((double) (thisWeek - lastWeek) / lastWeek) * 100;
        } else if (thisWeek > 0) {
            change = 100.0;
        }

        String changeStr = (change >= 0)
                ? String.format("▲ **+%.1f%%**", change)
                : String.format("▼ **%.1f%%**", change);

        return String.format("- **%s:** %d (so với %d tuần trước - %s)\n", metricName, thisWeek, lastWeek, changeStr);
    }

    private boolean isSignificantChange(long current, long previous) {
        if (previous == 0 && current > 5) return true;
        if (previous > 0) {
            double changeRatio = Math.abs((double) (current - previous) / previous);
            return changeRatio > 0.20;
        }
        return false;
    }

    private Map<String, Object> generateComparisonChart(long... values) {
        Map<String, Object> chart = new HashMap<>();
        chart.put("type", "bar");

        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("Lượt xem", "Yêu thích", "Đánh giá mới"));

        Map<String, Object> lastWeekDataset = new HashMap<>();
        lastWeekDataset.put("label", "Tuần trước");
        lastWeekDataset.put("data", List.of(values[1], values[3], values[5]));
        lastWeekDataset.put("backgroundColor", "rgba(156, 163, 175, 0.6)");

        Map<String, Object> thisWeekDataset = new HashMap<>();
        thisWeekDataset.put("label", "Tuần này");
        thisWeekDataset.put("data", List.of(values[0], values[2], values[4]));
        thisWeekDataset.put("backgroundColor", "rgba(79, 70, 229, 0.6)");

        data.put("datasets", List.of(lastWeekDataset, thisWeekDataset));
        chart.put("data", data);

        Map<String, Object> options = new HashMap<>();
        options.put("responsive", true);
        options.put("plugins", Map.of("title", Map.of("display", true, "text", "So sánh hiệu suất theo tuần")));
        options.put("scales", Map.of("y", Map.of("beginAtZero", true)));
        chart.put("options", options);

        return chart;
    }

    public List<ProactiveInsight> getUnreadInsights(Integer companyAccountId) {
        Account company = accountRepository.findById(companyAccountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty"));
        return insightRepository.findByCompanyAndIsReadFalseOrderByCreatedAtDesc(company);
    }

    @Transactional
    public void markAsRead(Integer insightId, Integer companyAccountId) {
        ProactiveInsight insight = insightRepository.findById(insightId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy insight"));
        if (!Objects.equals(insight.getCompany().getUserId(), companyAccountId)) {
            throw new SecurityException("Bạn không có quyền truy cập insight này.");
        }
        insight.setRead(true);
        insightRepository.save(insight);
    }
}