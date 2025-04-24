package com.sparta.paymentservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "m_payments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    private UUID reservationId;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String status;

    public static Payments create(UUID reservationId, BigDecimal totalPrice, String status) {
        return Payments.builder()
                .reservationId(reservationId)
                .totalPrice(totalPrice)
                .status(status)
                .build();
    }
}
