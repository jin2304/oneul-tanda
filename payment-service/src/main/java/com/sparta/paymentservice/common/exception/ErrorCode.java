package com.sparta.paymentservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    PAYMENT_CONFLICT(HttpStatus.CONFLICT, "이미 결제 처리된 건입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 요청을 처리할 수 없습니다.");

    private final HttpStatus errorCode;
    private final String message;
}
