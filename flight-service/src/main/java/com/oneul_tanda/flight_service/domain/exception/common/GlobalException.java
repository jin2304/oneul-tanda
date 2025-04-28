package com.oneul_tanda.flight_service.domain.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException {
    // 예외를 처리하기 위한 Custom Exception
    private final HttpStatus status;
    private final ErrorMessage errorMessage;

    public GlobalException(HttpStatus status, ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
