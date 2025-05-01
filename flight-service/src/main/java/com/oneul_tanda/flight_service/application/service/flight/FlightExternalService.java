package com.oneul_tanda.flight_service.application.service.flight;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import com.oneul_tanda.flight_service.domain.exception.common.GlobalException;
import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import com.oneul_tanda.flight_service.domain.exception.common.InvalidRequestException;
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
import org.springframework.http.HttpStatus;
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
    @Cacheable(value = "flightOffers",
            key = "#departureAirportCode + '#' + #arrivalAirportCode + '#' + #departureDate.toLocalDate().toString() + '#' + #requiredSeats")
    public List<FlightResponse> searchFlights(String departureAirportCode, String arrivalAirportCode,
                                              LocalDateTime departureDate, Integer requiredSeats, String userRole
    ) {
        validateUserRole(userRole);
        // 과거 날짜 검증(검색하고 있는 현재 시점보다 과거일 경우 예외 발생)
        checkDepartureDateInPast(departureDate);
        // 입력값 검증
        validateInputs(departureDate, requiredSeats);

        try {
            FlightOfferSearch[] offers = fetchFlightOffers(departureAirportCode, arrivalAirportCode,
                    departureDate.toLocalDate().toString(), requiredSeats);
            return extractFlightResponses(offers, false);
        } catch (Exception e) {
            log.error("Error while searching flights: {}", e.getMessage());
            throw new GlobalException(HttpStatus.BAD_GATEWAY, ErrorMessage.EXTERNAL_API_ERROR);
        }
    }

    @Cacheable(value = "flightOffers",
            key = "#departureAirportCode + '#' + #arrivalAirportCode + '#' + #departureDate.toLocalDate().toString() + '#' + #requiredSeats")
    public List<FlightResponse> fetchAndSaveFlights(
            String departureAirportCode, String arrivalAirportCode,
            LocalDateTime departureDate, Integer requiredSeats
    ) {
        // 과거 날짜 검증(검색하고 있는 현재 시점보다 과거일 경우 예외 발생)
        checkDepartureDateInPast(departureDate);
        // 입력값 검증
        validateInputs(departureDate, requiredSeats);

        try {
            FlightOfferSearch[] offers = fetchFlightOffers(departureAirportCode, arrivalAirportCode,
                    departureDate.toLocalDate().toString(), requiredSeats);
            return extractFlightResponses(offers, true);
        } catch (Exception e) {
            log.error("Error while searching and saving flights: {}", e.getMessage());
            throw new GlobalException(HttpStatus.BAD_GATEWAY, ErrorMessage.EXTERNAL_API_ERROR);
        }
    }

    private FlightOfferSearch[] fetchFlightOffers(
            String departureAirportCode, String arrivalAirportCode,
            String departureDate, Integer requiredSeats
    ) {
        try {
            return amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", departureAirportCode)
                            .and("destinationLocationCode", arrivalAirportCode)
                            .and("departureDate", departureDate)
                            .and("adults", requiredSeats)
                            .and("travelClass", "ECONOMY")
                            .and("max", 10));
        } catch (Exception e) {
            log.error("Error while fetching flight offers: {}", e.getMessage());
            throw new GlobalException(HttpStatus.BAD_GATEWAY, ErrorMessage.EXTERNAL_API_ERROR);
        }
    }

    private List<FlightResponse> extractFlightResponses(
            FlightOfferSearch[] offers, boolean saveToDb
    ) {
        if (offers == null) {
            return new ArrayList<>();
        }

        List<FlightResponse> flights = new ArrayList<>();
        Set<String> uniqueFlights = new HashSet<>();

        for (FlightOfferSearch offer : offers) {
            FlightOfferSearch.Itinerary[] itineraries = offer.getItineraries();
            if (itineraries == null || itineraries.length == 0) {
                continue;
            }

            FlightOfferSearch.SearchSegment[] segments = itineraries[0].getSegments();
            if (segments == null || segments.length == 0) {
                continue;
            }

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
            if (!uniqueFlights.add(flightKey)) {
                continue;
            }

            if (saveToDb) {
                FlightEntity flight = saveOrUpdateFlight(flightNum, carrierCode, departureAirportCode,
                        arrivalAirportCode,
                        departureTime, arrivalTime, price, remainingSeats);
                flights.add(FlightResponse.from(flight));
            } else {
                flights.add(FlightResponse.from(flightNum, carrierCode, departureAirportCode, arrivalAirportCode,
                        departureTime, arrivalTime, price, remainingSeats));
            }
        }
        return flights;
    }

    private FlightEntity saveOrUpdateFlight(
            String flightNum, String carrierCode, String departureAirportCode,
            String arrivalAirportCode, LocalDateTime departureTime,
            LocalDateTime arrivalTime, BigDecimal price, Integer remainingSeats
    ) {
        AirlineEntity airline = getAirlineByCode(carrierCode);

        Optional<FlightEntity> existingFlight = getExistingFlight(flightNum, departureTime, arrivalTime);

        FlightEntity flight;

        if (existingFlight.isEmpty()) {
            AirportEntity departureAirport = getDepartureAirport(departureAirportCode);
            AirportEntity arrivalAirport = getArrivalAirport(arrivalAirportCode);

            flight = FlightEntity.from(
                    flightNum, airline, departureAirport, arrivalAirport,
                    departureTime, arrivalTime, Duration.between(departureTime, arrivalTime),
                    price, remainingSeats);
        } else {
            flight = existingFlight.get();
            flight.updateFromOffer(price, remainingSeats);
        }
        return flightRepository.save(flight);
    }

    private Optional<FlightEntity> getExistingFlight(String flightNum, LocalDateTime departureTime,
                                                     LocalDateTime arrivalTime) {
        return flightRepository.findByFlightNumAndDepartureDateAndArrivalDate(
                flightNum, departureTime, arrivalTime);
    }

    private AirlineEntity getAirlineByCode(String carrierCode) {
        return airlineRepository.findByCode(carrierCode)
                .orElseGet(() -> airlineRepository.save(AirlineEntity.from(carrierCode, "Unknown Airline")));
    }

    private AirportEntity getArrivalAirport(String arrivalAirportCode) {
        return airportRepository.findByCode(arrivalAirportCode)
                .orElseThrow(InvalidRequestException::new);
    }

    private AirportEntity getDepartureAirport(String departureAirportCode) {
        return airportRepository.findByCode(departureAirportCode)
                .orElseThrow(InvalidRequestException::new);
    }

    private void checkDepartureDateInPast(LocalDateTime departureDate) {
        LocalDateTime now = LocalDateTime.now();
        if (departureDate.toLocalDate().isBefore(now.toLocalDate())) {
            throw new InvalidRequestException();
        }
    }

    private void validateUserRole(String userRole) {
        if (userRole.equals("CUSTOMER")) {
            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
        }
    }

    private void validateInputs(LocalDateTime departureDate, Integer requiredSeats) {
        if (departureDate == null) {
            throw new InvalidRequestException();
        }
        if (requiredSeats == null || requiredSeats <= 0) {
            requiredSeats = 1;
        }
    }
}