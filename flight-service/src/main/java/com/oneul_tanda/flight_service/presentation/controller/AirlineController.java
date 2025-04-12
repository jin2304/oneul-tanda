package com.oneul_tanda.flight_service.presentation.controller;

import com.oneul_tanda.flight_service.application.service.AirlineService;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airline.CreateAirlineRequest;
import com.oneul_tanda.flight_service.presentation.dtos.airline.UpdateAirlineRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping
    public ResponseEntity<AirlineResponse> createAirline(CreateAirlineRequest request) {
        AirlineResponse response = airlineService.createAirline(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{airlineId}")
    public ResponseEntity<AirlineResponse> updateAirline(
            @PathVariable UUID airlineId, UpdateAirlineRequest request
    ) {
        AirlineResponse response = airlineService.updateAirline(request.toCommand(airlineId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
