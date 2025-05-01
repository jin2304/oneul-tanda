package com.sparta.paymentservice.web;

import com.siot.IamportRestClient.request.CardInfo;
import com.sparta.paymentservice.application.dto.PaymentRequestDto;
import com.sparta.paymentservice.application.dto.PaymentResponseDto;
import com.sparta.paymentservice.application.service.PaymentService;
import com.sparta.paymentservice.common.util.TestCardInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@RequestBody PaymentRequestDto request) {
        CardInfo card = TestCardInfo.testCard1();
        PaymentResponseDto response = paymentService.confirmPayment(request, card);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cancel/{reservationId}")
    public  ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable UUID reservationId) {
        PaymentResponseDto response = paymentService.cancelPayment(reservationId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
