package com.example.DATN.exception;

import com.example.DATN.utils.enums.responsecode.BaseResponse;
import com.example.DATN.utils.enums.responsecode.ErrorCode;
import com.example.DATN.utils.enums.responsecode.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // mặc định

        switch ((ErrorCode) ex.getErrorCode()) {
            case USER_NOT_FOUND:
            case ACCOUNT_NOT_FOUND:
            case LOCATION_NOT_FOUND:
                status = HttpStatus.NOT_FOUND; // 404
                break;

            case INVALID_CREDENTIALS:
            case INVALID_TOKEN:
            case EXPIRED_TOKEN:
                status = HttpStatus.UNAUTHORIZED; // 401
                break;

            case ACCOUNT_NOT_ACTIVE:
                status = HttpStatus.FORBIDDEN; // 403
                break;

            case USERNAME_ALREADY_EXISTS:
                status = HttpStatus.CONFLICT; // 409
                break;

            default:
                status = HttpStatus.BAD_REQUEST; // 400
        }

        return ResponseEntity
                .status(status)
                .body(BaseResponse.error(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(new ResponseCode() {
                    @Override public int getCode() { return 500; }
                    @Override public String getMessage() { return "Lỗi hệ thống"; }
                }));
    }
}
