package com.oneul_tanda.flight_service.presentation.controller.external;

import com.oneul_tanda.flight_service.application.service.airline.AirlineService;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airline.CreateAirlineRequest;
import com.oneul_tanda.flight_service.presentation.dtos.airline.UpdateAirlineRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping("/{airlineId}")
    public ResponseEntity<AirlineResponse> getAirline(
            @PathVariable UUID airlineId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        AirlineResponse response = airlineService.getAirline(airlineId, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AirlineResponse>> searchAirlines(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Role") String userRole
    ) {
        Page<AirlineResponse> result = airlineService.searchAirlines(code, name, pageable, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping
    public ResponseEntity<AirlineResponse> createAirline(
            @RequestBody @Valid CreateAirlineRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        AirlineResponse response = airlineService.createAirline(request.toCommand(), userId, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{airlineId}")
    public ResponseEntity<AirlineResponse> updateAirline(
            @PathVariable UUID airlineId,
            @RequestBody @Valid UpdateAirlineRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        AirlineResponse response = airlineService.updateAirline(request.toCommand(airlineId), userId, userRole);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{airlineId}")
    public ResponseEntity<Void> deleteAirline(
            @PathVariable UUID airlineId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        airlineService.deleteAirline(airlineId, userId, userRole);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
