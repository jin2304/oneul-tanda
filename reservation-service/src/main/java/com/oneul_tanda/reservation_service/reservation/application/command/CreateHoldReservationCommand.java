package com.oneul_tanda.reservation_service.reservation.application.command;

import java.util.UUID;

public record CreateHoldReservationCommand(
        UUID flightId,
        Long userId,
        int seatCount
) {
    public static CreateHoldReservationCommand of(
            UUID flightId,
            Long userId,
            int seatCount
    ) {
        return new CreateHoldReservationCommand(flightId, userId, seatCount);
    }
}




