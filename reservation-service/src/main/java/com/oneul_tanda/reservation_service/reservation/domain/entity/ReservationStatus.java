package com.oneul_tanda.reservation_service.reservation.domain.entity;

public enum ReservationStatus {
    PENDING,                    // 예약 대기
    PASSENGER_INFO_ENTERED,     // 탑승객 정보 입력 완료
    RESERVED,                   // 예약 완료 (결제 완료)

    CANCELED,                   // 예약 취소
    PAYMENT_FAILED,             // 결제 실패
    PAYMENT_CANCELED;           // 결제 취소

    public static ReservationStatus of(String status) {
        return valueOf(status);
    }
}
