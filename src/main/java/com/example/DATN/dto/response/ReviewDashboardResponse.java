package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewDashboardResponse {
    private Integer reviewId;
    private Integer rating;
    private String comment;
    private AccountStatus status;
    private String username;
    private String avatar;
    private String locationName;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // mới thêm
    private boolean isReply;
    private Integer parentReviewId;
}
