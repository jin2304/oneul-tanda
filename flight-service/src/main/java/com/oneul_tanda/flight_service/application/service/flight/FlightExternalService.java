package com.oneul_tanda.flight_service.application.service.flight;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import com.oneul_tanda.flight_service.domain.repository.flight.FlightRepository;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightExternalService {

    private final Amadeus amadeus;
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    // 실시간 항공편 정보 조회
    public List<FlightResponse> searchFlights(String departureAirportCode, String arrivalAirportCode,
                                              LocalDateTime departureDate, Integer requiredSeats) throws Exception {
        if (departureDate == null) {
            log.error("departureDate is null");
            throw new IllegalArgumentException("departureDate is required");
        }
        if (requiredSeats == null || requiredSeats <= 0) {
            log.warn("requiredSeats is invalid, defaulting to 1");
            requiredSeats = 1;
        }

        String formattedDepartureDate = departureDate.toLocalDate().toString();
        log.debug("Formatted departureDate: {}", formattedDepartureDate);

        FlightOfferSearch[] results = amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", departureAirportCode)
                        .and("destinationLocationCode", arrivalAirportCode)
                        .and("departureDate", formattedDepartureDate)
                        .and("adults", requiredSeats)
                        .and("travelClass", "ECONOMY")
                        .and("max", 10)
        );

        List<FlightResponse> flightResponses = mapToFlightResponseList(results);
        log.debug("Amadeus API results - count: {}", flightResponses.size());
        return flightResponses;
    }

    private List<FlightResponse> mapToFlightResponseList(FlightOfferSearch[] offers) {
        List<FlightResponse> flights = new ArrayList<>();
        Set<String> uniqueFlights = new HashSet<>();

        if (offers == null) {
            log.debug("No flight offers returned");
            return flights;
        }

        for (FlightOfferSearch offer : offers) {
            FlightOfferSearch.Itinerary[] itineraries = offer.getItineraries();
            if (itineraries == null || itineraries.length == 0) {
                log.debug("No itineraries in offer");
                continue;
            }

            FlightOfferSearch.SearchSegment[] segments = itineraries[0].getSegments();
            if (segments == null || segments.length == 0) {
                log.debug("No segments in itinerary");
                continue;
            }

            FlightOfferSearch.SearchSegment firstSegment = segments[0];
            FlightOfferSearch.SearchSegment lastSegment = segments[segments.length - 1];

            String flightNum = firstSegment.getCarrierCode() + firstSegment.getNumber();
            String carrierCode = firstSegment.getCarrierCode();
            LocalDateTime departureTime = LocalDateTime.parse(firstSegment.getDeparture().getAt());
            LocalDateTime arrivalTime = LocalDateTime.parse(lastSegment.getArrival().getAt());
            String price = new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP).toString();
            Integer remainingSeats = offer.getNumberOfBookableSeats();

            // 중복 체크
            String flightKey = flightNum + "#" + departureTime + "#" + arrivalTime + "#" + price;
            if (!uniqueFlights.add(flightKey)) {
                log.debug("Skipping duplicate flight: {}", flightKey);
                continue;
            }

            log.debug("Processing flight: {} from {} to {}, departure: {}, arrival: {}, price: {}, seats: {}",
                    flightNum, firstSegment.getDeparture().getIataCode(), lastSegment.getArrival().getIataCode(),
                    departureTime, arrivalTime, price, remainingSeats);

            FlightResponse flight = FlightResponse.from(
                    flightNum,
                    carrierCode,
                    firstSegment.getDeparture().getIataCode(),
                    lastSegment.getArrival().getIataCode(),
                    departureTime,
                    arrivalTime,
                    new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP),
                    remainingSeats
            );

            flights.add(flight);
        }

        return flights;
    }

    // 실시간 항공편 정보 조회 및 DB 저장
    public List<FlightResponse> searchAndSaveFlights(
            String departureAirportCode,
            String arrivalAirportCode,
            LocalDateTime departureDate,
            Integer requiredSeats
    ) throws Exception {
        if (requiredSeats == null || requiredSeats <= 0) {
            log.warn("requiredSeats is invalid, defaulting to 1");
            requiredSeats = 1;
        }

        FlightOfferSearch[] offers = amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", departureAirportCode)
                        .and("destinationLocationCode", arrivalAirportCode)
                        .and("departureDate", departureDate.toLocalDate().toString())
                        .and("adults", requiredSeats)
                        .and("travelClass", "ECONOMY")
                        .and("max", 10)
        );

        return handleFlightOffersAndSave(offers);
    }

    private List<FlightResponse> handleFlightOffersAndSave(FlightOfferSearch[] offers) {
        List<FlightResponse> flights = new ArrayList<>();
        Set<String> uniqueFlights = new HashSet<>();

        if (offers == null) {
            log.debug("No flight offers returned");
            return flights;
        }

        for (FlightOfferSearch offer : offers) {
            FlightOfferSearch.Itinerary[] itineraries = offer.getItineraries();
            if (itineraries == null || itineraries.length == 0) {
                log.debug("No itineraries in offer");
                continue;
            }

            FlightOfferSearch.SearchSegment[] segments = itineraries[0].getSegments();
            if (segments == null || segments.length == 0) {
                log.debug("No segments in itinerary");
                continue;
            }

            FlightOfferSearch.SearchSegment firstSegment = segments[0];
            FlightOfferSearch.SearchSegment lastSegment = segments[segments.length - 1];

            String flightNum = firstSegment.getCarrierCode() + firstSegment.getNumber();
            String carrierCode = firstSegment.getCarrierCode();
            LocalDateTime departureTime = LocalDateTime.parse(firstSegment.getDeparture().getAt());
            LocalDateTime arrivalTime = LocalDateTime.parse(lastSegment.getArrival().getAt());
            Duration duration = Duration.between(departureTime, arrivalTime);
            String price = new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP).toString();
            Integer remainingSeats = offer.getNumberOfBookableSeats();

            // 중복 체크
            String flightKey = flightNum + "#" + departureTime + "#" + arrivalTime + "#" + price;
            if (!uniqueFlights.add(flightKey)) {
                log.debug("Skipping duplicate flight: {}", flightKey);
                continue;
            }

            log.debug("Processing flight: {} from {} to {}, departure: {}, arrival: {}, price: {}, seats: {}",
                    flightNum, firstSegment.getDeparture().getIataCode(), lastSegment.getArrival().getIataCode(),
                    departureTime, arrivalTime, price, remainingSeats);

            // 항공사 조회 및 생성
            AirlineEntity airline = airlineRepository.findByCode(carrierCode)
                    .orElseGet(() -> {
                        log.warn("Airline code {} not found, creating new entry", carrierCode);
                        AirlineEntity newAirline = AirlineEntity.from(
                                carrierCode, "Unknown Airline"
                        );
                        return airlineRepository.save(newAirline);
                    });

            // 중복된 항공편 체크
            Optional<FlightEntity> existing = flightRepository.findByFlightNumAndDepartureDateAndArrivalDate(
                    flightNum, departureTime, arrivalTime);
            if (existing.isEmpty()) {
                try {
                    Airport departureAirport = airportRepository.findByCode(firstSegment.getDeparture().getIataCode())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid departure airport code"));
                    Airport arrivalAirport = airportRepository.findByCode(lastSegment.getArrival().getIataCode())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid arrival airport code"));

                    FlightEntity flight = FlightEntity.from(
                            flightNum,
                            airline,
                            departureAirport,
                            arrivalAirport,
                            departureTime,
                            arrivalTime,
                            duration,
                            new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP),
                            remainingSeats
                    );
                    flightRepository.save(flight);
                } catch (Exception e) {
                    log.error("Failed to save flight {}: {}", flightNum, e.getMessage());
                    continue;
                }
            }

            flights.add(FlightResponse.from(
                    flightNum,
                    carrierCode,
                    firstSegment.getDeparture().getIataCode(),
                    lastSegment.getArrival().getIataCode(),
                    departureTime,
                    arrivalTime,
                    new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP),
                    remainingSeats
            ));
        }

        return flights;
    }
}