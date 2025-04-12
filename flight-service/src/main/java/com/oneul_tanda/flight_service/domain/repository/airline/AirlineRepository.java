package com.oneul_tanda.flight_service.domain.repository.airline;

import com.oneul_tanda.flight_service.domain.entity.Airline;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface AirlineRepository{

    Airline save(Airline airline);

    @Query("SELECT al FROM Airline al WHERE al.code = :code AND al.deletedAt IS NULL")
    Optional<Airline> findByCode(String code);

    @Query("SELECT al FROM Airline al WHERE al.id = :airlineId AND al.deletedAt IS NULL")
    Optional<Airline> findById(UUID airlineId);
}
