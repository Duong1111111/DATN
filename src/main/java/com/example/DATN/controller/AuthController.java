package com.example.DATN.controller;

import com.example.DATN.dto.request.LoginRequest;
import com.example.DATN.dto.response.JwtResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.interfaces.AuthService;
import com.example.DATN.utils.components.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AccountRepository accountRepository, AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> isValidToken(HttpServletRequest request) {
        try {
            Account account = authService.validateTokenAndGetAccount(request);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}