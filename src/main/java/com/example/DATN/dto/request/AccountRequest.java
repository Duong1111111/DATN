package com.example.DATN.dto.request;

import com.example.DATN.utils.enums.options.AccountStatus;
import com.example.DATN.utils.enums.options.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
    private AccountStatus status;
}