package com.oneul_tanda.reservation_service.reservation.domain.repository;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;

public interface ReservationRepository {

    Reservation save(Reservation reservation);
}
