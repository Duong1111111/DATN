package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ReviewResponse {
    private Integer reviewId;
    private Integer rating;
    private String comment;
    private Boolean status;
    private Integer userId;
    private Integer locationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
