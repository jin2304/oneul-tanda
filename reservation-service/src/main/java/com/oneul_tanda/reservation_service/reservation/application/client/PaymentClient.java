package com.oneul_tanda.reservation_service.reservation.application.client;

import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.CreatePaymentInfo;
import java.math.BigDecimal;
import java.util.UUID;


public interface PaymentClient {

    /**
     * 결제 요청
     */
    CreatePaymentInfo confirmPayment(UUID reservationId, BigDecimal totalPrice);

}
