package com.oneul_tanda.reservation_service.common.exception.handler;

import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.common.exception.dto.ErrorResponseEntity;
import com.oneul_tanda.reservation_service.common.exception.code.ApiErrorCode;
import com.oneul_tanda.reservation_service.common.exception.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * 전역 예외 처리 클래스
 * -컨트롤러에서 발생하는 다양한 예외를 일관된 형식으로 응답하기 위해 사용됨
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사용자 정의 예외 처리
     * Handle CustomException
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponseEntity.of(errorCode));
    }



    /**
     * @Valid requestBody 검증 실패 처리
     * Handle MethodArgumentNotValidException (requestBody validation)
     *
     * - DTO에서 @NotBlank 등 validation 어노테이션 위반 시 발생
     * - 주로 JSON 본문에서 필드 누락 또는 형식 오류 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseEntity> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {

        List<ErrorResponseEntity.ErrorField> errorFields = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponseEntity.ErrorField(
                        fieldError.getField(),          // 필드명
                        fieldError.getDefaultMessage()  // 오류 메시지
                ))
                .toList();


        return ResponseEntity
                .badRequest()
                .body(ErrorResponseEntity.of(ApiErrorCode.INVALID_REQUEST, errorFields));
    }



    /**
     * @Validated parameter 검증 실패 처리
     * Handle ConstraintViolationException (parameter validation)
     *
     * - @RequestParam, @PathVariable 등에서 @NotBlank 등 validation 어노테이션 위반 시 발생
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseEntity> handleConstraintViolationException(ConstraintViolationException e) {

        List<ErrorResponseEntity.ErrorField> errorFields = e.getConstraintViolations()
                .stream()
                .map(v -> new ErrorResponseEntity.ErrorField(
                        v.getPropertyPath().toString(), // ex: param name
                        v.getMessage()
                ))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponseEntity.of(ApiErrorCode.INVALID_REQUEST, errorFields));
    }



    /**
     * 필수 요청 파라미터 누락 처리
     * Handle ConstraintViolationException (parameter validation)
     *
     * - @RequestParam(required = true) 값이 빠진 경우
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseEntity> handleConstraintViolationException(MissingServletRequestParameterException e) {
        String errorMessage = e.getParameterName() + "를(을) 입력해 주세요.";

        return ResponseEntity
                .badRequest()
                .body(ErrorResponseEntity.of(
                        ApiErrorCode.INVALID_REQUEST,
                        List.of(new ErrorResponseEntity.ErrorField(e.getParameterName(), errorMessage))
                ));
    }




    /**
     * 예상하지 못한 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> handleGeneralException(Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(ApiErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponseEntity.from(ApiErrorCode.INTERNAL_SERVER_ERROR, e));
    }



    /**
     * Throwable 처리
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseEntity> handleThrowable(Throwable e, HttpServletRequest request) {
        return ResponseEntity
                .status(ApiErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponseEntity.from(ApiErrorCode.INTERNAL_SERVER_ERROR, new Exception(e)));
    }



    /**
     * 잘못된 인자 예외
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseEntity> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseEntity.from(ApiErrorCode.INVALID_REQUEST, e));
    }
}
