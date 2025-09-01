package com.example.DATN.service.impls;

import com.example.DATN.dto.request.*;
import com.example.DATN.dto.response.AccountResponse;
import com.example.DATN.dto.response.CompanyResponse;
import com.example.DATN.dto.response.PendingAccountDetailResponse;
import com.example.DATN.dto.response.UserResponse;
import com.example.DATN.entity.Account;
import com.example.DATN.entity.Company;
import com.example.DATN.entity.User;
import com.example.DATN.exception.BusinessException;
import com.example.DATN.repository.AccountRepository;
import com.example.DATN.repository.NotificationRepository;
import com.example.DATN.service.interfaces.AccountService;
import com.example.DATN.utils.components.TimeAgoUtil;
import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TimeAgoUtil timeAgoUtil;
    private final NotificationRepository notificationRepository;
    private final ImageUploadService imageUploadService;


    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder, TimeAgoUtil timeAgoUtil1, NotificationRepository notificationRepository, ImageUploadService imageUploadService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.timeAgoUtil = timeAgoUtil1;
        this.notificationRepository = notificationRepository;
        this.imageUploadService = imageUploadService;
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getAllStaffAccounts() {
        return accountRepository.findAll()
                .stream()
                .filter(acc -> acc.getRole() == Role.STAFF) // lọc theo role
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public AccountResponse createAccount(AccountRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        Account account = toEntity(request);
        account.setRole(request.getRole() != null ? request.getRole() : Role.STAFF);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse updateAccount(Integer id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

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
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(Role.USER);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        User user = new User();
        user.setAccount(account);
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        timeAgoUtil.notifyUserRegistered(savedAccount.getUser());
        return toUserResponse(savedAccount);
    }

    @Override
    public CompanyResponse registerCompany(CompanyRegisterRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setRole(Role.COMPANY);
        account.setStatus(AccountStatus.PENDING);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setTaxCode(request.getTaxCode());
        company.setLocation(request.getLocation());
        company.setPhoneNumber(request.getPhoneNumber());
        company.setAccount(account);
        account.setCompany(company);

        Account savedAccount = accountRepository.save(account);
        timeAgoUtil.notifyCompanyRegistered(savedAccount.getCompany());
        return toCompanyResponse(savedAccount);
    }
    @Override
    public UserResponse updateUserInfo(Integer accountId, UserUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() != Role.USER || account.getUser() == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_USER);
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
        User user = account.getUser();
        if (request.getTravelStyles() != null) {
            user.setTravelStyles(request.getTravelStyles());
        }
        if (request.getInterests() != null) {
            user.setInterests(request.getInterests());
        }
        if (request.getBudget() != null) {
            user.setBudget(request.getBudget());
        }
        if (request.getCompanions() != null) {
            user.setCompanions(request.getCompanions());
        }
        account.setUpdatedAt(LocalDateTime.now());
        return toUserResponse(accountRepository.save(account));
    }
    @Override
    public CompanyResponse updateCompanyInfo(Integer accountId, CompanyUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() != Role.COMPANY || account.getCompany() == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_COMPANY);
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
        if (request.getLocation() != null){
            account.getCompany().setLocation(request.getLocation());
        }
        if (request.getPhoneNumber() != null){
            account.getCompany().setPhoneNumber(request.getPhoneNumber());
        }
        account.setUpdatedAt(LocalDateTime.now());

        return toCompanyResponse(accountRepository.save(account));
    }

    @Override
    public String updateAvatar(Integer accountId, MultipartFile file) throws IOException {
        // Upload ảnh lên GCS
        String avatarUrl = imageUploadService.uploadImage(file, "avatars");

        // Tìm account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Cập nhật avatar
        account.setAvatar(avatarUrl);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        return avatarUrl;
    }


    @Override
    public List<AccountResponse> getPendingAccounts() {
        return accountRepository.findByStatus(AccountStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public PendingAccountDetailResponse getPendingAccountDetail(Integer userId) {
        Account account = accountRepository.findByUserIdAndStatus(userId, AccountStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        PendingAccountDetailResponse response = new PendingAccountDetailResponse();
        response.setUserId(account.getUserId());
        response.setUsername(account.getUsername());
        response.setEmail(account.getEmail());
        response.setStatus(account.getStatus());
        response.setRole(account.getRole().name());
        response.setCreatedAt(account.getCreatedAt());


        if (account.getCompany() != null) {
            response.setCompanyName(account.getCompany().getCompanyName());
            response.setTaxCode(account.getCompany().getTaxCode());
            response.setLocation(account.getCompany().getLocation());
            response.setPhoneNumber(account.getCompany().getPhoneNumber());
        }
        return response;
    }


    @Override
    public AccountResponse approveCompanyAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() == Role.COMPANY && account.getStatus() == AccountStatus.PENDING) {
            account.setStatus(AccountStatus.ACTIVE);
            account.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(account);
        } else {
            throw new BusinessException(ErrorCode.ONLY_PENDING_COMPANY);
        }

        return toResponse(account);
    }

    @Override
    public AccountResponse rejectCompanyAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() == Role.COMPANY && account.getStatus() == AccountStatus.PENDING) {
            account.setStatus(AccountStatus.INACTIVE);
            account.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(account);
        } else {
            throw new BusinessException(ErrorCode.ONLY_PENDING_COMPANYRE);
        }

        return toResponse(account);
    }

    @Override
    public UserResponse getCurrentUser() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() != Role.USER) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_USER);
        }

        return toUserResponse(account);
    }

    @Override
    public CompanyResponse getCurrentCompany() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getRole() != Role.COMPANY) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_COMPANY);
        }

        return toCompanyResponse(account);
    }
    @Override
    public AccountResponse getCurrentAccount() {
        String username = getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        return toResponse(account);
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
            response.setTravelStyles(account.getUser().getTravelStyles());
            response.setInterests(account.getUser().getInterests());
            response.setBudget(account.getUser().getBudget());
            response.setCompanions(account.getUser().getCompanions());
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
            response.setLocation(account.getCompany().getLocation());
            response.setPhoneNumber(account.getCompany().getPhoneNumber());
        }

        return response;
    }

}
