package com.oneul_tanda.flight_service.domain.repository.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightRepositoryCustom {

    Page<FlightEntity> searchFlights(String departureAirport, String arrivalAirport,
                                     LocalDateTime departureDate, Integer requiredSeats,
                                     Pageable pageable);
}
