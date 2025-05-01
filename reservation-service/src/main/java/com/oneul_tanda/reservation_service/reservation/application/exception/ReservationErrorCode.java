package com.oneul_tanda.reservation_service.reservation.application.exception;

import com.oneul_tanda.reservation_service.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {


  // 예약 조회/접근 관련
  RESERVATION_NOT_FOUND("예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PASSENGERS_NOT_FOUND("탑승객 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  TICKET_NOT_FOUND("티켓을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  RESERVATION_ACCESS_UNAUTHORIZED("해당 예약에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

  // 예약 생성/중복
  RESERVATION_DUPLICATE("이미 해당 항공편에 대한 예약이 존재합니다.", HttpStatus.BAD_REQUEST),

  // 예약 취소 관련
  CANNOT_CANCEL_ALREADY_CANCELED("이미 취소된 예약입니다.", HttpStatus.BAD_REQUEST),
  CANNOT_CANCEL_AFTER_24H_CREATION("예약 생성 후 24시간이 지나 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
  CANNOT_CANCEL_WITHIN_72H_TO_DEPARTURE("출발 72시간 이내 항공편은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),

  // 예약 상태 오류
  INVALID_STATUS("예약 상태가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  RESERVATION_STATUS_INVALID("예약 상태가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

  // 외부 시스템
  FLIGHT_SEAT_NOT_ENOUGH("항공편 잔여 좌석이 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FLIGHT_SEAT_RESTORE_FAILED("항공편 좌석 복원이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  PAYMENT_NOT_ALLOWED("결제 가능한 예약 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
  PAYMENT_FAILED("결제 요청에 실패하였습니다.", HttpStatus.BAD_GATEWAY),

  HOLD_RESERVATION_EXPIRED("임시 예약이 만료되었습니다. 다시 예약을 시도해주세요.", HttpStatus.BAD_REQUEST),
  REDIS_SAVE_FAILED("Redis 저장에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  REDIS_LOAD_FAILED("Redis 데이터 읽기에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);





  private final String message;
  private final HttpStatus httpStatus;
}

