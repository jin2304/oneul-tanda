package com.oneul_tanda.flight_service.presentation.dtos.exception;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    //예외 응답을 일관된 구조로 생성
    private HttpStatus status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponse> createResponse(HttpStatus status, ErrorMessage errorMessage) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(status, errorMessage.getCode(), errorMessage.getMessage()));
    }
}
