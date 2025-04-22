package com.oneul_tanda.reservation_service.reservation.application.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetFlightInfo(
        UUID id,
        String flightNum,
        String airlineCode,
        String departureAirportCode,
        String arrivalAirportCode,
        LocalDateTime departureDate,
        LocalDateTime arrivalDate,
        String  formattedDuration,
        BigDecimal price,
        Integer remainingSeats
) {
}
