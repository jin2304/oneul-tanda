package com.oneultanda.userservice.domain.model;

public enum Role {
    ADMIN,
    MANAGER,
    CUSTOMER;

    public static Role of(String role) {
        return valueOf(role);
    }
}
