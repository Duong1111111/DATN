package com.example.DATN.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String idToken; // client gửi id_token từ Google
}
