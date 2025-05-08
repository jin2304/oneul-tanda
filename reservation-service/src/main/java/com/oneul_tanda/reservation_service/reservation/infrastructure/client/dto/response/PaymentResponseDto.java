package com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response;

import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.PaymentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID reservationId;
    private String paymentId;
    private BigDecimal totalPrice;
    private String status;

    public PaymentInfo toInfo() {
        return new PaymentInfo(
                reservationId,
                paymentId,
                totalPrice,
                status
        );
    }

}
