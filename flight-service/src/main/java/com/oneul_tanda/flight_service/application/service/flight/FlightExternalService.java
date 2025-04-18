package com.oneul_tanda.flight_service.application.service.flight;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import com.oneul_tanda.flight_service.application.service.CacheService;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightExternalService {

    private final Amadeus amadeus;
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final CacheService cacheService;

    // 실시간 항공편 정보 조회
    @Cacheable(value = "flightOffers", key = "#departureAirportCode + '#' + #arrivalAirportCode + '#' + #departureDate.toLocalDate().toString() + '#' + #requiredSeats")
    public List<FlightResponse> searchFlights(String departureAirportCode, String arrivalAirportCode,
                                              LocalDateTime departureDate, Integer requiredSeats) throws Exception {
        validateInputs(departureDate, requiredSeats);
        FlightOfferSearch[] offers = fetchFlightOffers(departureAirportCode, arrivalAirportCode,
                departureDate.toLocalDate().toString(), requiredSeats);
        return extractFlightResponses(offers, false);
    }

    @Cacheable(value = "flightOffers", key = "#departureAirportCode + '#' + #arrivalAirportCode + '#' + #departureDate.toLocalDate().toString() + '#' + #requiredSeats")
    public List<FlightResponse> searchAndSaveFlights(String departureAirportCode, String arrivalAirportCode,
                                                     LocalDateTime departureDate, Integer requiredSeats) throws Exception {
        validateInputs(departureDate, requiredSeats);
        FlightOfferSearch[] offers = fetchFlightOffers(departureAirportCode, arrivalAirportCode,
                departureDate.toLocalDate().toString(), requiredSeats);
        return extractFlightResponses(offers, true);
    }

    private void validateInputs(LocalDateTime departureDate, Integer requiredSeats) {
        if (departureDate == null) {
            throw new IllegalArgumentException("departureDate is required");
        }
        if (requiredSeats == null || requiredSeats <= 0) {
            requiredSeats = 1;
        }
    }

    private FlightOfferSearch[] fetchFlightOffers(String departureAirportCode, String arrivalAirportCode,
                                                  String departureDate, Integer requiredSeats) throws Exception {
        return amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", departureAirportCode)
                        .and("destinationLocationCode", arrivalAirportCode)
                        .and("departureDate", departureDate)
                        .and("adults", requiredSeats)
                        .and("travelClass", "ECONOMY")
                        .and("max", 10));
    }

    private List<FlightResponse> extractFlightResponses(FlightOfferSearch[] offers, boolean saveToDb) {
        if (offers == null) {
            return new ArrayList<>();
        }

        List<FlightResponse> flights = new ArrayList<>();
        Set<String> uniqueFlights = new HashSet<>();

        for (FlightOfferSearch offer : offers) {
            FlightOfferSearch.Itinerary[] itineraries = offer.getItineraries();
            if (itineraries == null || itineraries.length == 0) continue;

            FlightOfferSearch.SearchSegment[] segments = itineraries[0].getSegments();
            if (segments == null || segments.length == 0) continue;

            FlightOfferSearch.SearchSegment firstSegment = segments[0];
            FlightOfferSearch.SearchSegment lastSegment = segments[segments.length - 1];

            String flightNum = firstSegment.getCarrierCode() + firstSegment.getNumber();
            String carrierCode = firstSegment.getCarrierCode();
            String departureAirportCode = firstSegment.getDeparture().getIataCode();
            String arrivalAirportCode = lastSegment.getArrival().getIataCode();
            LocalDateTime departureTime = LocalDateTime.parse(firstSegment.getDeparture().getAt());
            LocalDateTime arrivalTime = LocalDateTime.parse(lastSegment.getArrival().getAt());
            BigDecimal price = new BigDecimal(offer.getPrice().getTotal()).setScale(2, RoundingMode.HALF_UP);
            Integer remainingSeats = offer.getNumberOfBookableSeats();

            String flightKey = flightNum + "#" + departureTime + "#" + arrivalTime + "#" + price;
            if (!uniqueFlights.add(flightKey)) continue;

            if (saveToDb) {
                FlightEntity flight = saveOrUpdateFlight(flightNum, carrierCode, departureAirportCode, arrivalAirportCode,
                        departureTime, arrivalTime, price, remainingSeats);
                flights.add(cacheService.cacheFlight(FlightResponse.from(flight)));
            } else {
                flights.add(FlightResponse.from(flightNum, carrierCode, departureAirportCode, arrivalAirportCode,
                        departureTime, arrivalTime, price, remainingSeats));
            }
        }
        return flights;
    }

    private FlightEntity saveOrUpdateFlight(String flightNum, String carrierCode, String departureAirportCode,
                                            String arrivalAirportCode, LocalDateTime departureTime,
                                            LocalDateTime arrivalTime, BigDecimal price, Integer remainingSeats) {
        AirlineEntity airline = airlineRepository.findByCode(carrierCode)
                .orElseGet(() -> airlineRepository.save(AirlineEntity.from(carrierCode, "Unknown Airline")));

        Optional<FlightEntity> existing = flightRepository.findByFlightNumAndDepartureDateAndArrivalDate(
                flightNum, departureTime, arrivalTime);
        FlightEntity flight;
        if (existing.isEmpty()) {
            Airport departureAirport = airportRepository.findByCode(departureAirportCode)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid departure airport code: " + departureAirportCode));
            Airport arrivalAirport = airportRepository.findByCode(arrivalAirportCode)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid arrival airport code: " + arrivalAirportCode));

            flight = FlightEntity.from(
                    flightNum, airline, departureAirport, arrivalAirport,
                    departureTime, arrivalTime, Duration.between(departureTime, arrivalTime),
                    price, remainingSeats);
        } else {
            flight = existing.get();
            flight.updateFromOffer(price, remainingSeats);
        }
        return flightRepository.save(flight);
    }
}