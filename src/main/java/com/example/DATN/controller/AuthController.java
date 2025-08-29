package com.example.DATN.controller;

import com.example.DATN.dto.request.GoogleLoginRequest;
import com.example.DATN.dto.request.LoginRequest;
import com.example.DATN.dto.response.JwtResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.interfaces.AuthService;
import com.example.DATN.utils.components.JwtTokenProvider;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${google.client.id}")
    private String googleClientId;


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

    private final String GOOGLE_CLIENT_ID = googleClientId;

    @PostMapping("/login/google")
    public JwtResponse loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Kiểm tra account trong DB
            Account account = accountRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // Nếu chưa có thì tạo mới
                        Account newAcc = new Account();
                        newAcc.setEmail(email);
                        newAcc.setUsername(email); // hoặc name
                        newAcc.setPassword(""); // Google login không dùng password
                        newAcc.setRole(Role.USER); // default role
                        newAcc.setStatus(AccountStatus.ACTIVE);
                        newAcc.setCreatedAt(LocalDateTime.now());
                        return accountRepository.save(newAcc);
                    });

            String token = jwtTokenProvider.generateTokenByUserName(account.getUsername());

            return new JwtResponse(token, account.getRole().name(), account.getUserId(), account.getUsername());

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}