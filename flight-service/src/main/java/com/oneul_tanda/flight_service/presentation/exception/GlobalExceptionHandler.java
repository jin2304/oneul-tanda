package com.oneul_tanda.flight_service.presentation.exception;

import com.oneul_tanda.flight_service.domain.exception.airline.AirlineDuplicatedException;
import com.oneul_tanda.flight_service.domain.exception.airline.AirlineNotFoundException;
import com.oneul_tanda.flight_service.domain.exception.airport.AirportDuplicatedException;
import com.oneul_tanda.flight_service.domain.exception.airport.AirportNotFoundException;
import com.oneul_tanda.flight_service.domain.exception.common.GlobalException;
import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import com.oneul_tanda.flight_service.presentation.dtos.exception.ErrorResponse;
import com.oneul_tanda.flight_service.domain.exception.common.ExternalApiException;
import com.oneul_tanda.flight_service.domain.exception.flight.FlightDuplicatedException;
import com.oneul_tanda.flight_service.domain.exception.flight.FlightNotFoundException;
import com.oneul_tanda.flight_service.domain.exception.common.InvalidRequestException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException e) {
       log.info("GlobalException: {}", e.getMessage(), e);
        return ErrorResponse.createResponse(e.getStatus(), e.getErrorMessage());
    }

    // Airline
    @ExceptionHandler(AirlineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAirlineNotFoundException(AirlineNotFoundException e) {
        return ErrorResponse.createResponse(HttpStatus.NOT_FOUND, ErrorMessage.AIRLINE_NOT_FOUND);
    }

    @ExceptionHandler(AirlineDuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleAirlineDuplicatedException(AirlineDuplicatedException e) {
        return ErrorResponse.createResponse(HttpStatus.CONFLICT, ErrorMessage.DUPLICATED_AIRLINE);
    }

    // Airport
    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAirportNotFoundException(AirportNotFoundException e) {
        return ErrorResponse.createResponse(HttpStatus.NOT_FOUND, ErrorMessage.AIRPORT_NOT_FOUND);
    }

    @ExceptionHandler(AirportDuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleAirportDuplicatedException(AirportDuplicatedException e) {
        return ErrorResponse.createResponse(HttpStatus.CONFLICT, ErrorMessage.DUPLICATED_AIRPORT);
    }

    // Flight
    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFlightNotFoundException(FlightNotFoundException e) {
        return ErrorResponse.createResponse(HttpStatus.NOT_FOUND, ErrorMessage.FLIGHT_NOT_FOUND);
    }

    @ExceptionHandler(FlightDuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleFlightDuplicatedException(FlightDuplicatedException e) {
        return ErrorResponse.createResponse(HttpStatus.CONFLICT, ErrorMessage.DUPLICATED_FLIGHT);
    }

    // Common
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e) {
        return ErrorResponse.createResponse(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REQUEST);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e) {
        return ErrorResponse.createResponse(HttpStatus.BAD_GATEWAY, ErrorMessage.EXTERNAL_API_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
