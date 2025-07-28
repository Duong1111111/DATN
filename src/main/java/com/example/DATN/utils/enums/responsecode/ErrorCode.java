package com.example.DATN.utils.enums.responsecode;

public enum ErrorCode implements ResponseCode{
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng"),
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