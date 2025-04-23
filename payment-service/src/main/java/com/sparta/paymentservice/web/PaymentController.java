package com.sparta.paymentservice.web;

import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.service.TossPaymentService;
import com.sparta.paymentservice.infrastructure.client.toss.event.PaymentRequest;
import com.sparta.paymentservice.infrastructure.client.toss.event.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final TossPaymentService tossPaymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequestDto request) {
        PaymentResponse response = tossPaymentService.confirmPayment(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
