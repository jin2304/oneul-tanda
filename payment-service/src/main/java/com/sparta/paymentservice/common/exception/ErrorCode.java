package com.sparta.paymentservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND_PAID(HttpStatus.NOT_FOUND, "성공한 결제 정보를 찾을 수 없습니다."),
    PAYMENT_CONFLICT(HttpStatus.CONFLICT, "이미 결제 처리된 건입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 요청을 처리할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
