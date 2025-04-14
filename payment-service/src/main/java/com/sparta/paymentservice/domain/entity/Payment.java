package com.sparta.paymentservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table("m_payments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
}
