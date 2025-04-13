package com.oneul_tanda.flight_service.domain.repository.airline;

import com.oneul_tanda.flight_service.domain.entity.Airline;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface AirlineRepository{

    Airline save(Airline airline);

    @Query("SELECT al FROM Airline al WHERE al.code = :code AND al.deletedAt IS NULL")
    Optional<Airline> findByCode(String code);

    @Query("SELECT al FROM Airline al WHERE al.id = :airlineId AND al.deletedAt IS NULL")
    Optional<Airline> findById(UUID airlineId);

    @Query("SELECT al FROM Airline al WHERE al.code = :code AND al.name = :name AND al.deletedAt IS NULL")
    Page<Airline> findByCodeAndName(String code, String name, Pageable adjusted);
}
