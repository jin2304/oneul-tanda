package com.sparta.queueservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "flight-service")
public interface FlightClient {
    // 항공편을 조회 예시
    @GetMapping("/api/v1/flights/{flightId}")
    FlightResponse getFlight(@PathVariable UUID flightId);

    // 대기열 선점 성공시 좌석 수 차감 로직 api
    @PutMapping("/api/v1/flights/{flightId}/seats/decrease")
    void decreaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);
}
