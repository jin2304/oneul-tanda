package com.oneul_tanda.flight_service.presentation.controller;

import com.oneul_tanda.flight_service.presentation.dtos.UpdateAirportRequest;
import com.oneul_tanda.flight_service.application.service.AirportService;
import com.oneul_tanda.flight_service.presentation.dtos.CreateAirportRequest;
import com.oneul_tanda.flight_service.presentation.dtos.AirportResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping("/{airportId}")
    public ResponseEntity<AirportResponse> getAirport(
            @PathVariable UUID airportId
    ) {
        AirportResponse response = airportService.getAirport(airportId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AirportResponse>> searchAirports(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @PageableDefault Pageable pageable
    ) {
        Page<AirportResponse> result = airportService.searchAirports(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(
            @RequestBody @Valid CreateAirportRequest request
    ) {
        AirportResponse response = airportService.createAirport(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{airportId}")
    public ResponseEntity<AirportResponse> updateAirport(
            @PathVariable UUID airportId,
            @RequestBody @Valid UpdateAirportRequest request
    ) {
        AirportResponse response = airportService.updateAirport(request.toCommand(airportId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{airportId}")
    public ResponseEntity<Void> deleteAirport(
            @PathVariable UUID airportId
    ) {
        airportService.deleteAirport(airportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
