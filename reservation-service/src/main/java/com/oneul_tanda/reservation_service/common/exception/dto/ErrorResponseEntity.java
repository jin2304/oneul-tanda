package com.oneul_tanda.reservation_service.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oneul_tanda.reservation_service.common.exception.code.ErrorCode;
import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponseEntity(
        int status,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ErrorField> errors
) {


    public static ErrorResponseEntity of(ErrorCode errorCode, List<ErrorField> errors) {
        return new ErrorResponseEntity(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                errors
        );
    }


    public static ErrorResponseEntity of(ErrorCode errorCode) {
        return new ErrorResponseEntity(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                null
        );
    }


    public static ErrorResponseEntity from(ErrorCode errorCode, Exception ex) {
        return new ErrorResponseEntity(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                List.of(new ErrorField("exception", ex.getMessage()))
        );
    }


    public record ErrorField(
            Object value,
            String message
    ) {}
}
