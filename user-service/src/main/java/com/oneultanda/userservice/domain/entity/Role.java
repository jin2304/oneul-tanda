package com.oneultanda.userservice.domain.entity;

public enum Role {
    ADMIN,
    MANAGER,
    CUSTOMER;

    public static Role of(String role) {
        return valueOf(role);
    }
}
