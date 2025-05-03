package com.oneul_tanda.reservation_service.reservation.presentation.dto;

import java.util.UUID;

public record DeleteReservationResponseDto(
        UUID reservationId
) {

    public static DeleteReservationResponseDto of(UUID reservationId) {
        return new DeleteReservationResponseDto(
                reservationId
        );
    }
}
