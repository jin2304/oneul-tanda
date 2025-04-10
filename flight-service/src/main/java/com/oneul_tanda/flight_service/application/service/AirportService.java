package com.oneul_tanda.flight_service.application.service;

import com.oneul_tanda.flight_service.application.dtos.AirportCommand;
import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.repository.AirportRepository;
import com.oneul_tanda.flight_service.application.dtos.AirportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportRepository airportRepository;

    @Transactional
    public AirportResponse createAirport(AirportCommand airportCommand) {

        if (airportRepository.findByCode(airportCommand.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airport code " + airportCommand.getCode() + " already exists");
        }

        Airport airport = Airport.from(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

        airportRepository.save(airport);

        return AirportResponse.of(airport);
    }
}
