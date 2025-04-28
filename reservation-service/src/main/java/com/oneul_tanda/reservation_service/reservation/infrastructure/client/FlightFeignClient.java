package com.oneul_tanda.reservation_service.reservation.infrastructure.client;

import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response.FlightDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "flight-service")
public interface FlightFeignClient {

    /**
     * 항공편 조회
     */
    @GetMapping("/api/v1/flights/{flightId}")
    ResponseEntity<FlightDetailResponse> getFlight(@PathVariable UUID flightId);


    /**
     * 좌석 차감
     */
    @PutMapping("/internal/v1/flights/{flightId}/seats/decrease")
    ResponseEntity<Void> decreaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);


    /**
     * 좌석 복구
     */
    @PutMapping("/internal/v1/flights/{flightId}/seats/increase")
    ResponseEntity<Void> increaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);
}
