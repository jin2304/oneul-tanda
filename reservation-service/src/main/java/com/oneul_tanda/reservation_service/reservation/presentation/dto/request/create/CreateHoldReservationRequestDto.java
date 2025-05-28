package com.oneul_tanda.reservation_service.reservation.presentation.dto.request.create;

import java.util.UUID;

public record CreateHoldReservationRequestDto(
        UUID flightId,
        int seatCount
) {
}
