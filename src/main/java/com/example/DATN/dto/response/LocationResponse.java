package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class LocationResponse {
    private Integer locationId;
    private String name;
    private String description;
    private String location;
    private Double price;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String image;
    private Boolean status;
    private String categoryName;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}