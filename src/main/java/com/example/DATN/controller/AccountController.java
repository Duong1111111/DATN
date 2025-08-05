package com.example.DATN.controller;

import com.example.DATN.dto.request.*;
import com.example.DATN.dto.response.AccountResponse;
import com.example.DATN.dto.response.CompanyResponse;
import com.example.DATN.dto.response.PendingAccountDetailResponse;
import com.example.DATN.dto.response.UserResponse;
import com.example.DATN.service.interfaces.AccountService;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    public AccountResponse createAccount(@RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @PutMapping("/{id}")
    public AccountResponse updateAccount(@PathVariable Integer id, @RequestBody AccountRequest request) {
        return accountService.updateAccount(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
    }
    // --- Đăng ký tài khoản người dùng (user)
    @PostMapping("/register/user")
    public UserResponse registerUser(@RequestBody UserRegisterRequest request) {
        return accountService.registerUser(request);
    }

    // --- Đăng ký tài khoản công ty (status = PENDING)
    @PostMapping("/register/company")
    public CompanyResponse registerCompany(@RequestBody CompanyRegisterRequest request) {
        return accountService.registerCompany(request);
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponse> updateUserInfo(
            @PathVariable Integer id,
            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateUserInfo(id, request));
    }
    @PutMapping("/company/{id}")
    public ResponseEntity<CompanyResponse> updateCompanyInfo(
            @PathVariable Integer id,
            @RequestBody CompanyUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateCompanyInfo(id, request));
    }

    // --- Lấy danh sách tài khoản công ty đang chờ duyệt
    @GetMapping("/pending")
    public List<AccountResponse> getPendingAccounts() {
        return accountService.getPendingAccounts();
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<PendingAccountDetailResponse> getPendingAccountDetail(@PathVariable Integer userId) {
        return ResponseEntity.ok(accountService.getPendingAccountDetail(userId));
    }

    // --- Duyệt hoặc từ chối tài khoản công ty
    @PutMapping("/{id}/status")
    public AccountResponse updateAccountStatus(@PathVariable Integer id, @RequestParam AccountStatus status) {
        return accountService.updateAccountStatus(id, status);
    }
    @GetMapping("/me/user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = accountService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    // Lấy thông tin tài khoản company hiện tại
    @GetMapping("/me/company")
    public ResponseEntity<CompanyResponse> getCurrentCompany() {
        CompanyResponse company = accountService.getCurrentCompany();
        return ResponseEntity.ok(company);
    }
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getCurrentAccount() {
        AccountResponse account = accountService.getCurrentAccount();
        return ResponseEntity.ok(account);
    }
}
