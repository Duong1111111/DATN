package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewRequest {
    private Integer rating;
    private String comment;
    private AccountStatus status;
    private List<MultipartFile> images;
    private Integer userId;
    private Integer locationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Long> removeImageIds;
}