package com.sparta.paymentservice.infrastructure.jpa;

import com.sparta.paymentservice.domain.entity.Payment;
import com.sparta.paymentservice.domain.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long>, PaymentRepository {
}
