package com.oneul_tanda.reservation_service.reservation.application.client.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentInfo(
        UUID id,
        String paymentId,
        BigDecimal totalPrice,
        String status
) {
}
