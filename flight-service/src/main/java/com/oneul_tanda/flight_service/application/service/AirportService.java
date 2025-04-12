package com.oneul_tanda.flight_service.application.service;

import com.oneul_tanda.flight_service.application.dtos.AirportCommand;
import com.oneul_tanda.flight_service.presentation.dtos.AirportResponse;
import com.oneul_tanda.flight_service.application.dtos.UpdateAirportCommand;
import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.repository.AirportRepository;
import com.oneul_tanda.flight_service.domain.repository.AirportRepositoryCustom;
import com.oneul_tanda.flight_service.util.PagingUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportRepositoryCustom airportRepositoryCustom;

    public AirportResponse getAirport(UUID airportId) {

        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

        return AirportResponse.from(airport);
    }

    public Page<AirportResponse> searchAirports(String keyword, Pageable pageable) {
        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<Airport> airports = airportRepositoryCustom.searchByKeyword(keyword, adjusted);

        return airports.map(AirportResponse::from);
    }

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

        return AirportResponse.from(airport);
    }

    @Transactional
    public AirportResponse updateAirport(UpdateAirportCommand airportCommand) {

        Airport airport = airportRepository.findById(airportCommand.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

        airport.updateOf(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

        airport.updateModificationInfo("수정자");

        return AirportResponse.from(airport);
    }

    @Transactional
    public void deleteAirport(UUID airportId) {

        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

        airport.updateDeletionInfo("삭제자");
    }
}
