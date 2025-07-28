package com.example.DATN.service.impls;

import com.example.DATN.dto.request.*;
import com.example.DATN.dto.response.AccountResponse;
import com.example.DATN.dto.response.CompanyResponse;
import com.example.DATN.dto.response.UserResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Company;
import com.example.DATN.entity.User;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.service.interfaces.AccountService;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        Account account = toEntity(request);
        account.setRole(request.getRole() != null ? request.getRole() : Role.STAFF);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse updateAccount(Integer id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getUsername() != null) {
            account.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            account.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }
        account.setUpdatedAt(LocalDateTime.now());

        return toResponse(accountRepository.save(account));
    }

    @Override
    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }
    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(Role.USER);
        account.setStatus(AccountStatus.ACTIVE); // Mặc định là hoạt động
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        User user = new User();
        user.setHobby(request.getHobby());
        user.setAccount(account);
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        return toUserResponse(savedAccount);
    }

    @Override
    public CompanyResponse registerCompany(CompanyRegisterRequest request) {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(Role.COMPANY);
        account.setStatus(AccountStatus.PENDING); // Chờ admin duyệt
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setTaxCode(request.getTaxCode());
        company.setAccount(account);
        account.setCompany(company);

        Account savedAccount = accountRepository.save(account);
        return toCompanyResponse(savedAccount);
    }
    @Override
    public UserResponse updateUserInfo(Integer accountId, UserUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() != Role.USER || account.getUser() == null) {
            throw new RuntimeException("Account is not a user");
        }

        if (request.getUsername() != null) {
            account.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }
        if (request.getHobby() != null) {
            account.getUser().setHobby(request.getHobby());
        }
        account.setUpdatedAt(LocalDateTime.now());
        return toUserResponse(accountRepository.save(account));
    }
    @Override
    public CompanyResponse updateCompanyInfo(Integer accountId, CompanyUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() != Role.COMPANY || account.getCompany() == null) {
            throw new RuntimeException("Account is not a company");
        }

        if (request.getUsername() != null) {
            account.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }
        if (request.getCompanyName() != null) {
            account.getCompany().setCompanyName(request.getCompanyName());
        }
        if (request.getTaxCode() != null) {
            account.getCompany().setTaxCode(request.getTaxCode());
        }
        account.setUpdatedAt(LocalDateTime.now());

        return toCompanyResponse(accountRepository.save(account));
    }


    @Override
    public List<AccountResponse> getPendingAccounts() {
        return accountRepository.findByStatus(AccountStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse updateAccountStatus(Integer accountId, AccountStatus newStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() == Role.COMPANY && account.getStatus() == AccountStatus.PENDING) {
            account.setStatus(newStatus);
            account.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Only pending company accounts can be updated");
        }

        return toResponse(account);
    }
    @Override
    public UserResponse getCurrentUser() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() != Role.USER) {
            throw new RuntimeException("Account is not a user");
        }

        return toUserResponse(account);
    }

    @Override
    public CompanyResponse getCurrentCompany() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getRole() != Role.COMPANY) {
            throw new RuntimeException("Account is not a company");
        }

        return toCompanyResponse(account);
    }
    @Override
    public AccountResponse getCurrentAccount() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return toResponse(account); //convert từ Account -> AccountResponse
    }

    // Helper method lấy username từ SecurityContext
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getName();
    }


    // --- Manual Mapping Methods ---
    private Account toEntity(AccountRequest request) {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(request.getRole());
        account.setStatus(request.getStatus() != null ? request.getStatus() : AccountStatus.ACTIVE);
        return account;
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getUserId());
        response.setUsername(account.getUsername());
        response.setEmail(account.getEmail());
        response.setRole(account.getRole());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        response.setStatus(account.getStatus());
        return response;
    }
    private UserResponse toUserResponse(Account account) {
        UserResponse response = new UserResponse();
        response.setAccountId(account.getUserId());
        response.setUsername(account.getUsername());
        response.setEmail(account.getEmail());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        if (account.getUser() != null) {
            response.setHobby(account.getUser().getHobby());
        }

        return response;
    }

    private CompanyResponse toCompanyResponse(Account account) {
        CompanyResponse response = new CompanyResponse();
        response.setAccountId(account.getUserId());
        response.setUsername(account.getUsername());
        response.setEmail(account.getEmail());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        if (account.getCompany() != null) {
            response.setCompanyName(account.getCompany().getCompanyName());
            response.setTaxCode(account.getCompany().getTaxCode());
        }

        return response;
    }

}
