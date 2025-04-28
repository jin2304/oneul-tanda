package com.oneul_tanda.flight_service.presentation.controller.internal;

import com.oneul_tanda.flight_service.application.service.flight.FlightService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/flights/{flightId}/seats")
public class InternalController {

    private final FlightService flightService;

    // 좌석 차감
    @PutMapping("/decrease")
    public ResponseEntity<Void> decreaseSeats(
            @PathVariable UUID flightId,
            @RequestParam Integer requiredSeats
    ) {
        flightService.decreaseSeats(flightId, requiredSeats);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 좌석 복구
    @PutMapping("/increase")
    public ResponseEntity<Void> increaseSeats(
            @PathVariable UUID flightId,
            @RequestParam Integer requiredSeats
    ) {
        flightService.increaseSeats(flightId, requiredSeats);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
