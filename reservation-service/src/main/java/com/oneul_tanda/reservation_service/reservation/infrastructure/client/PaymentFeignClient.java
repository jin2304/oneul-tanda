package com.oneul_tanda.reservation_service.reservation.infrastructure.client;


import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.request.PaymentRequestDto;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;


@FeignClient(name = "payment-service")
public interface PaymentFeignClient {

    /**
     * 결제 요청
     */
    @PostMapping("/api/v1/payments/confirm")
    ResponseEntity<PaymentResponseDto> confirmPayment(@RequestBody PaymentRequestDto request);


    /**
     * 결제 취소
     */
    @PostMapping("/api/v1/payments/cancel/{reservationId}")
    ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable UUID reservationId);
}
