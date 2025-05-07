package com.oneul_tanda.reservation_service.reservation.application.client;

import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.PaymentInfo;
import java.math.BigDecimal;
import java.util.UUID;


public interface PaymentClient {

    /**
     * 결제 요청
     */
    PaymentInfo confirmPayment(UUID reservationId, BigDecimal totalPrice);


    /**
     * 결제 취소
     */
    PaymentInfo cancelPayment(UUID reservationId);
}
