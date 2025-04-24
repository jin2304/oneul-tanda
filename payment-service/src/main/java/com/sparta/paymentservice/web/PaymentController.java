package com.sparta.paymentservice.web;

import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;
import com.sparta.paymentservice.application.service.ImPortPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final ImPortPaymentService imPortPaymentService;

    @PostMapping("/test")
    public ResponseEntity<PaymentResponseDto> requestTestPayment(@RequestBody PaymentRequestDto request) {

     PaymentResponseDto response = imPortPaymentService.confirmPayment(request);

     return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
