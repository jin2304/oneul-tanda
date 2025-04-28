package com.sparta.paymentservice.common.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public PaymentException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
