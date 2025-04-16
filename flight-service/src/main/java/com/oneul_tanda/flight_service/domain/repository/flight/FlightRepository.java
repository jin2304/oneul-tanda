package com.oneul_tanda.flight_service.domain.repository.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface FlightRepository {

    FlightEntity save(FlightEntity flight);

    @Query("""
                SELECT f FROM FlightEntity f
                WHERE f.flightNum = :flightNum
                  AND f.departureDate = :departureDate
                  AND f.deletedAt IS NULL
            """)
    Optional<FlightEntity> findByFlightNumAndDepartureDate(String flightNum, LocalDateTime departureDate);

    @Query("SELECT f FROM FlightEntity f WHERE f.id = :flightId AND f.deletedAt IS NULL")
    Optional<FlightEntity> findById(UUID flightId);

    @Query("""
                SELECT f FROM FlightEntity f
                WHERE f.flightNum = :flightNum
                  AND f.departureDate = :departureTime
                  AND f.arrivalDate = :arrivalTime
                  AND f.deletedAt IS NULL
            """)
    Optional<FlightEntity> findByFlightNumAndDepartureDateAndArrivalDate(
            String flightNum, LocalDateTime departureTime, LocalDateTime arrivalTime);
}
