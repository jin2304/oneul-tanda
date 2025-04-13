package com.oneul_tanda.flight_service.presentation.controller;

import com.amadeus.resources.Location;
import com.oneul_tanda.flight_service.application.service.AirlineExternalService;
import com.oneul_tanda.flight_service.application.service.AirportExternalService;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineSearchResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportSearchResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/amadeus")
public class AmadeusController {

    private final AirportExternalService airportExternalService;
    private final AirlineExternalService airlineExternalService;

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
}
