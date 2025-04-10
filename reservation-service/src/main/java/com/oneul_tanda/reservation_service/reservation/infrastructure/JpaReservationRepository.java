package com.oneul_tanda.reservation_service.reservation.infrastructure;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaReservationRepository
        extends ReservationRepository, JpaRepository<Reservation, UUID> {
}
