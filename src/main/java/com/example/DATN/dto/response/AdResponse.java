package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AdResponse {
    private Integer adId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String title;
    private String description;
    private List<String> actions;
    private Double budget;
    private PaymentStatus paymentStatus;
    private List<String> categories;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByUsername;
    private String locationName;
    private Integer locationId; // ID để tạo link chi tiết
    private String locationAddress; // Địa chỉ đầy đủ
    private List<String> locationImages; // Danh sách ảnh của địa điểm
    private Double averageRating; // Rating trung bình
    private Integer totalReviews; // Tổng số lượt đánh giá
}