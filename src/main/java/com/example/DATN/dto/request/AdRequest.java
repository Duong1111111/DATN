package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private Boolean status;
    private Integer createdById;
    private Integer locationId;
}