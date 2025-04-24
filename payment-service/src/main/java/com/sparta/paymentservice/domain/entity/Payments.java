package com.sparta.paymentservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Integer totalPrice;


    public static Payments create(Integer totalPrice, UUID reservationId) {
        return Payments.builder()
                .totalPrice(totalPrice)
                .reservationId(reservationId)
                .build();
    }
}
