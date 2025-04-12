package com.oneul_tanda.flight_service.domain.repository;

import com.oneul_tanda.flight_service.domain.entity.Airport;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface AirportRepository {

    Airport save(Airport airport);

    @Query("SELECT a FROM Airport a WHERE a.code = :code AND a.deletedAt IS NULL")
    Optional<Airport> findByCode(String code);

    @Query("SELECT a FROM Airport a WHERE a.id = :airportId AND a.deletedAt IS NULL")
    Optional<Airport> findById(UUID airportId);
}
