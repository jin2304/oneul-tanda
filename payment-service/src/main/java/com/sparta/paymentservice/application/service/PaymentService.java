package com.sparta.paymentservice.application.service;

import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto confirmPayment(PaymentRequestDto request);
}
