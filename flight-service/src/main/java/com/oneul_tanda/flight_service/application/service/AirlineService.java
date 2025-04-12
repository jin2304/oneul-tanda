package com.oneul_tanda.flight_service.application.service;

import com.oneul_tanda.flight_service.application.dtos.airline.CreateAirlineCommand;
import com.oneul_tanda.flight_service.application.dtos.airline.UpdateAirlineCommand;
import com.oneul_tanda.flight_service.domain.entity.Airline;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import java.util.UUID;
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

        if (airlineRepository.findByCode(airlineCommand.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airline code " + airlineCommand.getCode() + " already exists");
        }

        Airline airline = Airline.from(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );

        airlineRepository.save(airline);

        return AirlineResponse.from(airline);
    }

    @Transactional
    public AirlineResponse updateAirline(UpdateAirlineCommand airlineCommand) {

        Airline airline = airlineRepository.findById(airlineCommand.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

        airline.updateOf(
                airlineCommand.getName(),
                airlineCommand.getCode()
        );

        airline.updateModificationInfo("수정자");

        return AirlineResponse.from(airline);
    }

    @Transactional
    public void deleteAirline(UUID airlineId) {

        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

        airline.updateDeletionInfo("삭제자");
    }
}
