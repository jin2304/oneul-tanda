package com.sparta.paymentservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private String reservationId;
    private String paymentId;
    private Integer amount;
    private String status;
}
