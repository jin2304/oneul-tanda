package com.sparta.paymentservice.common.exception;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.sparta.paymentservice.common.dto.ErrorResponse;
import com.sparta.paymentservice.common.dto.IamPortErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IamPortException.class)
    public ResponseEntity<IamPortErrorResponse> ImportPaymentExceptionHandler(IamPortException e) {
        log.info("IamPortException error: {}", e.getMessage());
        return new ResponseEntity<>(
                IamPortErrorResponse.from(e.getErrorCode(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<IamPortErrorResponse> ImportResponseExceptionHandler(IamportResponseException e) {
        log.info("ImportResponseException error: {}", e.getMessage());
        return new ResponseEntity<>(
                IamPortErrorResponse.from(e.getHttpStatusCode(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> PaymentExceptionHandler(PaymentException e) {
        log.info("PaymentException error: {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.from(e.getErrorCode()),
                e.getErrorCode().getStatus());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> IOExceptionHandler(IOException e) {
        log.info("IOException error: {}", e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
