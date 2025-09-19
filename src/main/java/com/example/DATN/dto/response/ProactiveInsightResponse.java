package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ProactiveInsightResponse {
    private Integer id;
    private String title;
    private String summary;
    private LocalDateTime createdAt;
    private Map<String, Object> details;
}