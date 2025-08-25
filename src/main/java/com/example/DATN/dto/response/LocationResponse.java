package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class LocationResponse {
    private Integer locationId;
    private String name;
    private String description;
    private String location;
    private String price;
    private LocalTime openTime;
    private LocalTime closeTime;
    private List<String> images;
    private String website;
    private Integer phoneNumber;
    private AccountStatus status;
    private List<String> categoryNames;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}