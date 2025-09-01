package com.example.DATN.service.interfaces;

import com.example.DATN.dto.request.*;
import com.example.DATN.dto.response.AccountResponse;
import com.example.DATN.dto.response.CompanyResponse;
import com.example.DATN.dto.response.PendingAccountDetailResponse;
import com.example.DATN.dto.response.UserResponse;
import com.example.DATN.utils.enums.options.AccountStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AccountService {

    List<AccountResponse> getAllAccounts();

    List<AccountResponse> getAllStaffAccounts();

    AccountResponse createAccount(AccountRequest request);

    AccountResponse updateAccount(Integer id, AccountRequest request);

    void deleteAccount(Integer id);

    UserResponse registerUser(UserRegisterRequest request);
    CompanyResponse registerCompany(CompanyRegisterRequest request);

    UserResponse updateUserInfo(Integer accountId, UserUpdateRequest request);

    CompanyResponse updateCompanyInfo(Integer accountId, CompanyUpdateRequest request);

    String updateAvatar(Integer accountId, MultipartFile file) throws IOException;

    List<AccountResponse> getPendingAccounts();

    PendingAccountDetailResponse getPendingAccountDetail(Integer userId);

    AccountResponse approveCompanyAccount(Integer accountId);

    AccountResponse rejectCompanyAccount(Integer accountId);

    UserResponse getCurrentUser();

    CompanyResponse getCurrentCompany();

    AccountResponse getCurrentAccount();
}