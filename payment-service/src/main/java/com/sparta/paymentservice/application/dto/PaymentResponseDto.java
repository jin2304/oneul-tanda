package com.sparta.paymentservice.application.dto;

import com.siot.IamportRestClient.response.Payment;
import com.sparta.paymentservice.domain.entity.Payments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID reservationId;
    private String paymentId;
    private BigDecimal totalPrice;
    private String status;

    public static PaymentResponseDto toDto(Payment payment) {
        return PaymentResponseDto.builder()
                .paymentId(payment.getImpUid())
                .reservationId(UUID.fromString(payment.getMerchantUid()))
                .totalPrice(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
    public Payments toEntity() {
        return Payments.create(
                this.reservationId,
                this.totalPrice,
                this.status
        );
    }
}
