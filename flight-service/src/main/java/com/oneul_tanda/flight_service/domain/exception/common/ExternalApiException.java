package com.oneul_tanda.flight_service.domain.exception.common;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException() {
        super(ErrorMessage.EXTERNAL_API_ERROR.getMessage());
    }
}
