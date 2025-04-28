package com.sparta.paymentservice.domain.repository;

import com.sparta.paymentservice.domain.entity.Payments;

import java.util.UUID;

public interface PaymentRepository {
    Payments save(Payments payments);
    Payments findByReservationId(UUID reservationId);
}
