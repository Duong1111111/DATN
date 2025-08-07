package com.example.DATN.exception;

import com.example.DATN.utils.enums.responsecode.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ResponseCode errorCode;

  public BusinessException(ResponseCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

}
