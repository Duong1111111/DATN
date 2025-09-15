package com.example.DATN.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GrowthDTO {
    private String period;
    private Long count;
}
