package com.example.DATN.dto.response;

import com.example.DATN.utils.enums.options.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Integer accountId;
    private String username;
    private String email;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> travelStyles;
    private List<String> interests;
    private String budget;
    private List<String> companions;
}
