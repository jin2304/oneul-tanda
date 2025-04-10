package com.oneul_tanda.flight_service.infrastructure.repository;

import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.repository.AirportRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportJpaRepository extends JpaRepository<Airport, UUID>, AirportRepository {

}
