package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update;


import java.util.UUID;

public record CancelReservationResponseDto(
        UUID reservationId
) {
    public static CancelReservationResponseDto of(UUID reservationId) {
        return new CancelReservationResponseDto(
                reservationId
        );
    }
}
