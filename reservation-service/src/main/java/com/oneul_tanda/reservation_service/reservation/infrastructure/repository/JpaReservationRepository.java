package com.oneul_tanda.reservation_service.reservation.infrastructure.repository;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaReservationRepository
        extends ReservationRepository, JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r JOIN r.ticketList t " +
            "WHERE r.userId = :userId AND t.flightId = :flightId AND r.status != 'CANCELED'")
    Optional<Reservation> findByUserIdAndFlightId(UUID userId, UUID flightId);

}
