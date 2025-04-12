package com.oneul_tanda.flight_service.presentation.controller;

import com.amadeus.resources.Location;
import com.oneul_tanda.flight_service.presentation.dtos.AirportResponse;
import com.oneul_tanda.flight_service.presentation.dtos.AirportSearchResponse;
import com.oneul_tanda.flight_service.application.service.AirportExternalService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/external/airports")
public class AmadeusController {

    private final AirportExternalService amadeusService;

    // 실시간 조회용 Amadeus API
    @GetMapping("/live-search")
    public ResponseEntity<List<AirportSearchResponse>> searchAirports(@RequestParam String keyword) throws Exception {
        Location[] locations = amadeusService.searchAirports(keyword);
        List<AirportSearchResponse> response = Arrays.stream(locations)
                .map(AirportSearchResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    // 실시간 조회 및 DB 저장용 Amadeus API
    @PostMapping("/live-search")
    public ResponseEntity<List<AirportResponse>> searchAndSave(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(amadeusService.searchAndSave(keyword));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
