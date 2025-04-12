package com.oneul_tanda.flight_service.domain.repository.airline;

import com.oneul_tanda.flight_service.domain.entity.AirLine;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface AirlineRepository{

    AirLine save(AirLine airLine);

    @Query("SELECT al FROM AirLine al WHERE al.code = :code AND al.deletedAt IS NULL")
    Optional<AirLine> findByCode(String code);

    @Query("SELECT al FROM AirLine al WHERE al.id = :airlineId AND al.deletedAt IS NULL")
    Optional<AirLine> findById(UUID airlineId);
}
