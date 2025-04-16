package com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetFlightInfo(
        UUID id,
        LocalDateTime departureDate,
        LocalDateTime arrivalDate,
        BigDecimal price
) {
}
