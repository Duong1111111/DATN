package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.LoginRequest;
import com.example.DATN.dto.response.JwtResponse;
import com.example.DATN.entity.Account;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    JwtResponse login(LoginRequest request);

    Account validateTokenAndGetAccount(HttpServletRequest request);
}
