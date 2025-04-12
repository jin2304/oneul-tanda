package com.oneul_tanda.flight_service.application.service;

import com.oneul_tanda.flight_service.application.dtos.airline.CreateAirlineCommand;
import com.oneul_tanda.flight_service.domain.entity.AirLine;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Transactional
    public AirlineResponse createAirline(CreateAirlineCommand airlineCommand) {

        if(airlineRepository.findByCode(airlineCommand.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airline code " + airlineCommand.getCode() + " already exists");
        }

        AirLine airLine = AirLine.from(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );

        airlineRepository.save(airLine);

        return AirlineResponse.from(airLine);
    }
}
