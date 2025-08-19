package com.example.DATN.utils.enums.responsecode;

public enum ErrorCode implements ResponseCode{
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng"),
    USERNAME_ALREADY_EXISTS(1002, "Username already exists"),
    ACCOUNT_NOT_FOUND(1003,"Account not found"),
    ACCOUNT_NOT_USER(1004,"Account is not a user"),
    ACCOUNT_NOT_COMPANY(1005,"Account is not a company"),
    ONLY_PENDING_COMPANY(1006,"Only pending company accounts can be approved"),
    LOCATION_NOT_FOUND(1007,"location not found"),
    INVALID_TOKEN(2001, "Token không hợp lệ"),
    EXPIRED_TOKEN(2002, "Hết phiên đăng nhập");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}