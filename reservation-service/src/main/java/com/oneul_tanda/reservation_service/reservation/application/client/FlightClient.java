package com.oneul_tanda.reservation_service.reservation.application.client;

import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


public interface FlightClient {

    /**
     * 항공편 조회
     */
    GetFlightInfo getFlight(@PathVariable UUID flightId);


    /**
     * 좌석 차감
     */
    void decreaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);


    /**
     * 좌석 복구
     */
    void increaseSeats(@PathVariable UUID flightId, @RequestParam Integer requiredSeats);
}
