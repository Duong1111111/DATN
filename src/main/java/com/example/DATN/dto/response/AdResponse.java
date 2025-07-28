package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdResponse {
    private Integer adId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByUsername;
    private String locationName;
}