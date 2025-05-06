package com.oneul_tanda.reservation_service.reservation.domain.entity.vo;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender of(String gender) {
        return valueOf(gender);
    }
}
