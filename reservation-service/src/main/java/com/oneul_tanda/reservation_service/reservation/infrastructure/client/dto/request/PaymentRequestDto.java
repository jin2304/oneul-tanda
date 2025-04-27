package com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private UUID reservationId;
    private BigDecimal totalPrice;

    public static PaymentRequestDto of(UUID reservationId, BigDecimal totalPrice) {
        return new PaymentRequestDto(
                reservationId,
                totalPrice
        );
    }
}
