package com.oneul_tanda.reservation_service.reservation.domain.repository;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(UUID reservationId);

    Page<Reservation> findAll(Pageable pageable);
}
