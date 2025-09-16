package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewReplyResponse {
    private Integer reviewId;
    private String comment;
    private String username;
    private String avatar;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
