package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Action;
import com.example.DATN.utils.enums.options.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AdRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
//    private Double budget;
    private String title;
    private String description;
    private List<Action> actions;
    private List<Integer> categoryIds;
    private AccountStatus status;
    private Integer createdById;
    private Integer locationId;
    private PaymentStatus paymentStatus;
}