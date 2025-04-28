package com.oneul_tanda.flight_service.domain.exception.common;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() {
        super(ErrorMessage.INVALID_REQUEST.getMessage());
    }
}
