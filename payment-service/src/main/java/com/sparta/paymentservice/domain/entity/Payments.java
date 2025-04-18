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

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private TypeEnum paymentType;

    @Column(nullable = false)
    private StatusEnum paymentStatus;

    public static Payments create(UUID paymentId,
                                  Integer totalPrice,
                                  TypeEnum paymentType,
                                  StatusEnum paymentStatus) {
        return Payments.builder()
                .paymentId(paymentId)
                .totalPrice(totalPrice)
                .paymentType(paymentType)
                .paymentStatus(paymentStatus)
                .build();
    }
}
