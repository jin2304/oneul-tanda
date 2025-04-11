package com.oneul_tanda.reservation_service.passenger.domain.entity;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender of(String gender) {
        return valueOf(gender);
    }
}
