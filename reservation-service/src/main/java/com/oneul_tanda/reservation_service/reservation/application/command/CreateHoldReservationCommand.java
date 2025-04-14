package com.oneul_tanda.reservation_service.reservation.application.command;

import java.util.UUID;

public record CreateHoldReservationCommand(
        UUID flightId,
        UUID userId,
        int seatCount
) {
    public static CreateHoldReservationCommand of(
            UUID flightId,
            String  userId,
            int seatCount
    ) {
        return new CreateHoldReservationCommand(flightId,  UUID.fromString(userId), seatCount);
    }
}




