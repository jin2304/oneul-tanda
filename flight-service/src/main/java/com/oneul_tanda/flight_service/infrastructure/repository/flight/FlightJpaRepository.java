package com.oneul_tanda.flight_service.infrastructure.repository.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import com.oneul_tanda.flight_service.domain.repository.flight.FlightRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightJpaRepository extends JpaRepository<FlightEntity, UUID>, FlightRepository {
}
