package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
    private List<String> travelStyles;
    private List<String> interests;
    private String budget;
    private List<String> companions;
    private String fullName;     // Họ tên đầy đủ
    private String phoneNumber;
}
