package com.example.DATN.service.impls;

import com.example.DATN.dto.request.LoginRequest;
import com.example.DATN.dto.response.JwtResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.interfaces.AuthService;
import com.example.DATN.utils.components.JwtTokenProvider;
import com.example.DATN.utils.enums.options.AccountStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accountRepository = accountRepository;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản chưa được kích hoạt hoặc đã bị khóa");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateTokenByUserName(request.getUsername());
        // lấy role từ account
        String role = account.getRole().name();

        return new JwtResponse(token, role, account.getUserId(), account.getUsername());
    }

    @Override
    public Account validateTokenAndGetAccount(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (!jwtTokenProvider.isValidToken(token)) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn");
        }

        String username = jwtTokenProvider.getUserNameByToken(request);

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản chưa được kích hoạt hoặc đã bị khóa");
        }

        return account;
    }

}
