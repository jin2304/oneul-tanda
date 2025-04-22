package com.oneul_tanda.reservation_service.reservation.application.client;

import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


public interface FlightClient {

    /**
     * 항공편 조회
     */
    @GetMapping("/api/v1/flights/{flightId}")
    GetFlightInfo getFlight(@PathVariable UUID flightId);


    /**
     * 좌석 차감
     */
    @PutMapping("/api/v1/flights/{flightId}/seats/decrease")
    void decreaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);


    /**
     * 좌석 복구
     */
    @PutMapping("/api/v1/flights/{flightId}/seats/increase")
    void increaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);
}
