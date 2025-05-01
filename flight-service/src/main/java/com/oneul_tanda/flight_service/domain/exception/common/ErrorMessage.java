package com.oneul_tanda.flight_service.domain.exception.common;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    // 예외 메시지 Enum으로 관리

    // Airline
    AIRLINE_NOT_FOUND("AIRLINE_NOT_FOUND", "해당 항공사를 찾을 수 없습니다."),
    DUPLICATED_AIRLINE("DUPLICATED_AIRLINE", "중복된 항공사입니다."),

    // Airport
    AIRPORT_NOT_FOUND("AIRPORT_NOT_FOUND", "해당 공항을 찾을 수 없습니다."),
    DUPLICATED_AIRPORT("DUPLICATED_AIRPORT", "중복된 공항입니다."),

    // Flight
    FLIGHT_NOT_FOUND("FLIGHT_NOT_FOUND", "해당 항공편을 찾을 수 없습니다."),
    DUPLICATED_FLIGHT("DUPLICATED_FLIGHT", "중복된 항공편입니다."),

    // Common
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    EXTERNAL_API_ERROR("EXTERNAL_API_ERROR", "외부 API 호출에 실패했습니다.");

    private final String code;
    private final String message;

    ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
