package com.oneul_tanda.reservation_service.common.exception;

import com.oneul_tanda.reservation_service.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public static CustomException from(ErrorCode errorCode) {
        return new CustomException(errorCode);
    }

}
