package com.example.DATN.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String role;
    private Integer userId;
    private String username;

    public JwtResponse(String token, String role, Integer userId, String username) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.username = username;
    }
}