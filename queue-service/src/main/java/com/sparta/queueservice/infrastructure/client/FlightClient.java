package com.sparta.queueservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service")
public interface FlightClient {
    // 항공편을 조회 예시
    @GetMapping("/api/v1/airports/{flightId}")
    FlightResponse getAirport(@PathVariable String flightId);

    // 대기열 선점 성공시 좌석 수 차감 로직 api
}
