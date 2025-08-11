package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyUpdateRequest {
    private String username;
    private String password;
    private String email;
    private String companyName;
    private String taxCode;
    private String location;
    private Integer phoneNumber;
}
