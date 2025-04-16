package com.oneul_tanda.reservation_service.reservation.infrastructure.client;

import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response.GetFlightInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "flight-service")
public interface FlightClient {

    /**
     * 항공편 조회
     */
    @GetMapping("/api/v1/flights/{flightId}")
    GetFlightInfo getFlight(@PathVariable UUID flightId);


    /**
     * 좌석 복구
     */
    @GetMapping("/api/v1/airports/{flightId}")
    ResponseEntity<Void> increaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);
}
