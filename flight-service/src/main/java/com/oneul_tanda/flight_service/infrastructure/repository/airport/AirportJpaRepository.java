package com.oneul_tanda.flight_service.infrastructure.repository.airport;

import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportJpaRepository extends JpaRepository<AirportEntity, UUID>, AirportRepository {

}
