package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ReviewRequest {
    private Integer rating;
    private String comment;
    private Boolean status;
    private Integer userId;
    private Integer locationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}