package com.oneul_tanda.flight_service.presentation.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<AirportSearchResponse>> searchAirports(@RequestParam String keyword) {
        try {
            Location[] locations = airportExternalService.searchAirports(keyword);
            List<AirportSearchResponse> response = Arrays.stream(locations)
                    .map(AirportSearchResponse::from)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while searching airports", e);  // 예외 로깅 추가
            return ResponseEntity.internalServerError().build();  // 서버 오류 상태 코드 반환
        }
    }

    // 실시간 공항 정보 조회 및 DB 저장용
    @PostMapping("/airports/fetch-and-save")
    public ResponseEntity<List<AirportResponse>> searchAndSaveAirports(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(airportExternalService.searchAndSaveAirports(keyword));
        } catch (Exception e) {
            log.error("Error fetching and saving airports: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // 실시간 항공사 정보 조회용
    @GetMapping("/airlines/live-search")
    public ResponseEntity<List<AirlineSearchResponse>> searchAirlines(@RequestParam String keyword) {
        try {
            List<AirlineSearchResponse> airlineResponses = airlineExternalService.searchAirlines(keyword);
            return ResponseEntity.ok(airlineResponses);
        } catch (Exception e) {
            log.error("Error while fetching airlines", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 실시간 항공사 정보 조회 및 DB 저장용
    @PostMapping("/airlines/fetch-and-save")
    public ResponseEntity<List<AirlineResponse>> searchAndSaveAirlines(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(airlineExternalService.searchAndSaveAirlines(keyword));
        } catch (Exception e) {
            log.error("Error fetching and saving airlines: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // 실시간 항공편 정보 조회용
    @GetMapping("/flights/live-search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam String departureAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam Integer requiredSeats
    ) {
        try {
            List<FlightResponse> flightResponses = flightExternalService.searchFlights(
                    departureAirportCode, arrivalAirportCode, departureDate, requiredSeats);
            return ResponseEntity.ok(flightResponses);
        } catch (Exception e) {
            log.error("Error while searching flights", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 실시간 항공편 정보 조회 및 DB 저장용
    @PostMapping("/flights/fetch-and-save")
    public ResponseEntity<List<FlightResponse>> searchAndSaveFlights(
            @RequestParam String departureAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam Integer requiredSeats
    ) {
        try {
            // 과거 날짜 검증
            LocalDateTime now = LocalDateTime.now();
            if (departureDate.toLocalDate().isBefore(now.toLocalDate())) {
                log.warn("Requested departureDate {} is in the past", departureDate);
                return ResponseEntity.badRequest().body(List.of());
            }
            List<FlightResponse> response = flightExternalService.searchAndSaveFlights(
                    departureAirportCode, arrivalAirportCode, departureDate, requiredSeats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while searching and saving flights", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
