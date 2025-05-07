package com.oneul_tanda.reservation_service.reservation.infrastructure.client.impl;

import com.oneul_tanda.reservation_service.reservation.application.client.PaymentClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.PaymentInfo;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.PaymentFeignClient;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.request.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public PaymentInfo confirmPayment(UUID reservationId, BigDecimal totalPrice) {
        return paymentFeignClient.confirmPayment(
                PaymentRequestDto.of(reservationId, totalPrice)).getBody().toInfo();
    }


    @Override
    public PaymentInfo cancelPayment(UUID reservationId) {
        return paymentFeignClient.cancelPayment(reservationId).getBody().toInfo();
    }
}
