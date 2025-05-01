package com.sparta.paymentservice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IamPortErrorResponse {
    private int errorCode;
    private String message;

    public static IamPortErrorResponse from(int errorCode, String message) {
        return new IamPortErrorResponse(errorCode, message);
    }
}
