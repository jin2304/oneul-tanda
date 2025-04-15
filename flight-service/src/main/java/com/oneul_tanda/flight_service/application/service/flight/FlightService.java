package com.oneul_tanda.flight_service.application.service.flight;

import com.oneul_tanda.flight_service.application.dtos.flight.CreateFlightCommand;
import com.oneul_tanda.flight_service.application.dtos.flight.UpdateFlightCommand;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import com.oneul_tanda.flight_service.domain.repository.flight.FlightRepository;
import com.oneul_tanda.flight_service.domain.repository.flight.FlightRepositoryCustom;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightDetailResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightResponse;
import com.oneul_tanda.flight_service.util.PagingUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightRepositoryCustom flightRepositoryCustom;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    public FlightDetailResponse getFlight(UUID flightId) {

        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        return FlightDetailResponse.from(flight);
    }


    public Page<FlightResponse> searchFlights(String departureAirport, String arrivalAirport,
                                              LocalDateTime departureDate, Integer requiredSeats,
                                              Pageable pageable
    ) {
        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<FlightEntity> flights = flightRepositoryCustom.searchFlights(
                departureAirport, arrivalAirport,
                departureDate, requiredSeats, adjusted
        );

        return flights.map(FlightResponse::from);
    }

    @Transactional
    public FlightResponse createFlight(CreateFlightCommand flightCommand) {

        if (flightRepository.findByFlightNumAndDepartureDate(flightCommand.getFlightNum(),
                        flightCommand.getDepartureDate())
                .isPresent()) {
            throw new IllegalArgumentException("Flight with this flight number and departure date already exists");
        }

        // AirlineEntity와 Airport 엔티티 조회
        AirlineEntity airline = airlineRepository.findByCode(flightCommand.getAirlineCode())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));
        Airport departureAirport = airportRepository.findByCode(flightCommand.getDepartureAirportCode())
                .orElseThrow(() -> new IllegalArgumentException("Departure Airport not found"));
        Airport arrivalAirport = airportRepository.findByCode(flightCommand.getArrivalAirportCode())
                .orElseThrow(() -> new IllegalArgumentException("Arrival Airport not found"));

        Duration duration = Duration.between(flightCommand.getDepartureDate(), flightCommand.getArrivalDate());

        FlightEntity flight = FlightEntity.from(
                flightCommand.getFlightNum(),
                airline,
                departureAirport,
                arrivalAirport,
                flightCommand.getDepartureDate(),
                flightCommand.getArrivalDate(),
                duration,
                flightCommand.getPrice(),
                flightCommand.getRemainingSeats()
        );

        flightRepository.save(flight);

        return FlightResponse.from(flight);
    }

    @Transactional
    public FlightResponse updateFlight(UpdateFlightCommand flightCommand) {

        FlightEntity flight = flightRepository.findById(flightCommand.getFlightId())
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        // AirlineEntity와 Airport 엔티티 조회
        AirlineEntity airline = airlineRepository.findByCode(flightCommand.getAirlineCode())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));
        Airport departureAirport = airportRepository.findByCode(flightCommand.getDepartureAirportCode())
                .orElseThrow(() -> new IllegalArgumentException("Departure Airport not found"));
        Airport arrivalAirport = airportRepository.findByCode(flightCommand.getArrivalAirportCode())
                .orElseThrow(() -> new IllegalArgumentException("Arrival Airport not found"));

        Duration duration = Duration.between(flightCommand.getDepartureDate(), flightCommand.getArrivalDate());

        flight.updateOf(
                flightCommand.getFlightNum(),
                airline,
                departureAirport,
                arrivalAirport,
                flightCommand.getDepartureDate(),
                flightCommand.getArrivalDate(),
                duration,
                flightCommand.getPrice(),
                flightCommand.getRemainingSeats()
        );

        flight.updateModificationInfo("수정자");

        return FlightResponse.from(flight);
    }

    @Transactional
    public void deleteFlight(UUID flightId) {

        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        flight.updateDeletionInfo("삭제자");
    }

    @Transactional
    public void decreaseSeats(UUID flightId, Integer requiredSeats) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        flight.decreaseSeatCount(requiredSeats);
    }

    @Transactional
    public void increaseSeats(UUID flightId, Integer requiredSeats) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        flight.increaseSeatCount(requiredSeats);
    }
}
