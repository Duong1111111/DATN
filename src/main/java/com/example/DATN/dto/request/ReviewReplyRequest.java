package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ReviewReplyRequest {
    private Integer parentReviewId; // review gốc
    private Integer userId;         // ai reply
    private String comment;         // nội dung reply

    private List<MultipartFile> images;
}
