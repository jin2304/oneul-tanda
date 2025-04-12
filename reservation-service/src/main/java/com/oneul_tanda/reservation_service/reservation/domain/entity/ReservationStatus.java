package com.oneul_tanda.reservation_service.reservation.domain.entity;

public enum ReservationStatus {
    PENDING,    // 예약 대기
    RESERVED,   // 예약 완료
    CANCELED;   // 예약 취소

    public static ReservationStatus of(String status) {
        return valueOf(status);
    }
}
