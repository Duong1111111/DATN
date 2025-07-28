package com.example.DATN.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class FavoriteResponse {
    private Integer favorId;
    private Integer userId;
    private Integer locationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean status;
}
