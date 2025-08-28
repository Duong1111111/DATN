package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PendingAccountDetailResponse {
    private Integer userId;
    private String username;
    private String email;
    private AccountStatus status;
    private String role;
    private LocalDateTime createdAt;

    private String companyName;
    private String taxCode;
    private String location;
    private String phoneNumber;
}
