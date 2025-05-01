package com.sparta.paymentservice.common.exception;

import lombok.Getter;

@Getter
public class IamPortException extends RuntimeException {
    private final int errorCode;
    private final String message;

    public IamPortException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}