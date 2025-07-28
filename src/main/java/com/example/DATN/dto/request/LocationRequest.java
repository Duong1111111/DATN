package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class LocationRequest {
    private String name;
    private String description;
    private String location;
    private Integer createdBy;
    private Integer categoryId;
    private Double price;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String image;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
