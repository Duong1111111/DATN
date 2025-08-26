package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserRegisterRequest {
    private String username;
    private String password;
    private String email;
}
