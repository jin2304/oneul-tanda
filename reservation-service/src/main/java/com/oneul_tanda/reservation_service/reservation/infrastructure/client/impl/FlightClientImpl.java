package com.oneul_tanda.reservation_service.reservation.infrastructure.client.impl;

import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.FlightFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlightClientImpl implements FlightClient {

    private final FlightFeignClient flightFeignClient;

    @Override
    public GetFlightInfo getFlight(UUID flightId) {
        return flightFeignClient.getFlight(flightId).getBody().toInfo();
    }

    @Override
    public void decreaseSeats(UUID flightId, Integer requiredSeats) {
        flightFeignClient.decreaseSeats(flightId, requiredSeats);
    }

    @Override
    public void increaseSeats(UUID flightId, Integer requiredSeats) {
        flightFeignClient.increaseSeats(flightId, requiredSeats);
    }
}
