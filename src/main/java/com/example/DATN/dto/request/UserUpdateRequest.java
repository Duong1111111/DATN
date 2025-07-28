package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
    private String hobby;
}
