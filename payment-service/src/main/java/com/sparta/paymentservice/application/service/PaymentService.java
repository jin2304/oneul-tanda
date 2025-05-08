package com.sparta.paymentservice.application.service;

import com.siot.IamportRestClient.request.CardInfo;
import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;
import com.sparta.paymentservice.infrastructure.kafka.event.ReservationCanceledEvent;

import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto confirmPayment(PaymentRequestDto request, CardInfo card);

    PaymentResponseDto cancelPayment(UUID reservationId);
    PaymentResponseDto cancelPaymentV2(ReservationCanceledEvent event);
}
