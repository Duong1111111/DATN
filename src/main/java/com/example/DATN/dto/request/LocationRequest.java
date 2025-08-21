package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class LocationRequest {
    private String name;
    private String description;
    private String location;
    private Integer createdBy;
    private Integer categoryId;
    private Double price;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String website;
    private Integer phoneNumber;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Danh sách ID ảnh cần xóa (nếu có)
    private List<Integer> imagesToDelete;
}
