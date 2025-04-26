package com.sparta.paymentservice.domain.repository;

import com.sparta.paymentservice.domain.entity.Payments;

public interface PaymentRepository {
    Payments save(Payments payments);
}
