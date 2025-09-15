package com.example.DATN.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyLocationSummary {
    private Integer year;
    private Integer month;
    private Long totalViews;
    private Long totalFavorites;
    private Long totalDirections;
}
