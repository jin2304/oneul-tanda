package com.sparta.paymentservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private UUID reservationId;
    private UUID userId;
    private Integer amount;
}
