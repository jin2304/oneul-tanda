package com.oneul_tanda.flight_service.presentation.controller.external;

import com.amadeus.resources.Location;
import com.oneul_tanda.flight_service.application.service.airline.AirlineExternalService;
import com.oneul_tanda.flight_service.application.service.airport.AirportExternalService;
import com.oneul_tanda.flight_service.application.service.flight.FlightExternalService;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineSearchResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportSearchResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amadeus")
public class AmadeusController {

    private final AirportExternalService airportExternalService;
    private final AirlineExternalService airlineExternalService;
    private final FlightExternalService flightExternalService;

    // 실시간 공항 정보 조회용
    @GetMapping("/airports/live-search")
    public ResponseEntity<List<AirportSearchResponse>> searchAirports(
            @RequestParam String keyword,
            @RequestHeader("X-User-Role") String userRole
    ) {
        Location[] locations = airportExternalService.searchAirports(keyword, userRole);
        List<AirportSearchResponse> response = Arrays.stream(locations)
                .map(AirportSearchResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 실시간 공항 정보 조회 및 DB 저장용
    @PostMapping("/airports/fetch-and-save")
    public ResponseEntity<List<AirportResponse>> fetchAndSaveAirports(
            @RequestParam String keyword,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(airportExternalService.fetchAndSaveAirports(keyword, userRole));
    }


    // 실시간 항공사 정보 조회용
    @GetMapping("/airlines/live-search")
    public ResponseEntity<List<AirlineSearchResponse>> searchAirlines(
            @RequestParam String keyword,
            @RequestHeader("X-User-Role") String userRole
    ) {
        List<AirlineSearchResponse> airlineResponses = airlineExternalService.searchAirlines(keyword, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(airlineResponses);
    }

    // 실시간 항공사 정보 조회 및 DB 저장용
    @PostMapping("/airlines/fetch-and-save")
    public ResponseEntity<List<AirlineResponse>> fetchAndSaveAirlines(
            @RequestParam String keyword,
            @RequestHeader("X-User-Role") String userRole
    ) {
        List<AirlineResponse> airlineResponses = airlineExternalService.fetchAndSaveAirlines(keyword, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(airlineResponses);
    }

    // 실시간 항공편 정보 조회용
    @GetMapping("/flights/live-search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam String departureAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam Integer requiredSeats,
            @RequestHeader("X-User-Role") String userRole
    ) {
        List<FlightResponse> flightResponses = flightExternalService.searchFlights(
                departureAirportCode, arrivalAirportCode, departureDate, requiredSeats, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(flightResponses);
    }

    // 실시간 항공편 정보 조회 및 DB 저장용
    @PostMapping("/flights/fetch-and-save")
    public ResponseEntity<List<FlightResponse>> fetchAndSaveFlights(
            @RequestParam String departureAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam Integer requiredSeats
    ) {
        List<FlightResponse> response = flightExternalService.fetchAndSaveFlights(
                departureAirportCode, arrivalAirportCode, departureDate, requiredSeats);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
