package com.example.DATN.utils.enums.responsecode;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse <T>{
    private boolean success;
    private int code = 200;
    private String message;
    private T result;

    public static <T> BaseResponse<T> success(ResponseCode code, String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .code(code.getCode())
                .message(message)
                .result(data)
                .build();
    }

    public static <T> BaseResponse<T> success(ResponseCode code, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .code(code.getCode())
                .message(code.getMessage())
                .result(data)
                .build();
    }

    public static <T> BaseResponse<T> success(ResponseCode code) {
        return BaseResponse.<T>builder()
                .success(true)
                .code(code.getCode())
                .message(code.getMessage())
                .result(null)
                .build();
    }

    public static <T> BaseResponse<T> error(ResponseCode code, String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code.getCode())
                .message(message)
                .result(null)
                .build();
    }

    public static <T> BaseResponse<T> error(ResponseCode code) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code.getCode())
                .message(code.getMessage())
                .result(null)
                .build();
    }
}
