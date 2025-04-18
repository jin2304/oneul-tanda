package com.oneul_tanda.flight_service.application.service;

import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    @CachePut(value = "airlines", key = "#result.code")
    public AirlineResponse cacheAirline(AirlineResponse response) {
        return response;
    }

    @CachePut(value = "airports", key = "#result.code")
    public AirportResponse cacheAirport(AirportResponse response) {
        return response;
    }

    @CachePut(value = "flights", key = "#result.id")
    public FlightResponse cacheFlight(FlightResponse result) {
        log.debug("Caching flight: flights:{}", result.getId());
        return result;
    }
}
