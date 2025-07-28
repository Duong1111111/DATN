package com.example.DATN.utils.enums.responsecode;

public enum SuccessCode implements ResponseCode{
    SUCCESSFUL(200, "Thành công")
    ;
    private int code;
    private String message;

    SuccessCode(int code, String message) {
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
