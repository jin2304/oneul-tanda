package com.oneul_tanda.flight_service.domain.repository;

import com.oneul_tanda.flight_service.domain.entity.Airport;
import java.util.Optional;

public interface AirportRepository {

    Airport save(Airport airport);

    Optional<Airport> findByCode(String code);
}
