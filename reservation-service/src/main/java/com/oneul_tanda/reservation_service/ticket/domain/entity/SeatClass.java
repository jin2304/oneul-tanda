package com.oneul_tanda.reservation_service.ticket.domain.entity;

public enum SeatClass {
    ECONOMY,    // 이코노미
    BUSINESS,   // 비즈니스
    FIRST;      // 퍼스트

    public static SeatClass of(String seatClass) {
        return valueOf(seatClass);
    }
}