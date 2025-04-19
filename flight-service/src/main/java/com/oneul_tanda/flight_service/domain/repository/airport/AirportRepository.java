package com.oneul_tanda.flight_service.domain.repository.airport;

import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface AirportRepository {

    AirportEntity save(AirportEntity airport);

    @Query("SELECT a FROM AirportEntity a WHERE a.code = :code AND a.deletedAt IS NULL")
    Optional<AirportEntity> findByCode(String code);

    @Query("SELECT a FROM AirportEntity a WHERE a.id = :airportId AND a.deletedAt IS NULL")
    Optional<AirportEntity> findById(UUID airportId);
}
