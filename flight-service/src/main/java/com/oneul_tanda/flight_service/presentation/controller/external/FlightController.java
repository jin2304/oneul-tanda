package com.oneul_tanda.flight_service.presentation.controller.external;

import com.oneul_tanda.flight_service.application.service.flight.FlightService;
import com.oneul_tanda.flight_service.presentation.dtos.flight.CreateFlightRequest;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightDetailResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.UpdateFlightRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDetailResponse> getFlight(
            @PathVariable UUID flightId
    ) {
        FlightDetailResponse response = flightService.getFlight(flightId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FlightResponse>> searchFlights(
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(required = false) Integer requiredSeats,
            @PageableDefault Pageable pageable,
            @RequestHeader("X-User-Role") String userRole
    ) {
        Page<FlightResponse> result = flightService.searchFlights(
                departureAirport, arrivalAirport,
                departureDate, requiredSeats,
                pageable, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(
            @RequestBody @Valid CreateFlightRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        FlightResponse response = flightService.createFlight(request.toCommand(), userId, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{flightId}")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable UUID flightId,
            @RequestBody @Valid UpdateFlightRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        FlightResponse response = flightService.updateFlight(request.toCommand(flightId), userId, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{flightId}")
    public ResponseEntity<Void> deleteFlight(
            @PathVariable UUID flightId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        flightService.deleteFlight(flightId, userId, userRole);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
