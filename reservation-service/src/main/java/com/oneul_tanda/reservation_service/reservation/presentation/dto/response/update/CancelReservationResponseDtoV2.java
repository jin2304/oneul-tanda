package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update;


import java.util.UUID;

public record CancelReservationResponseDtoV2(
        UUID reservationId
) {
    public static CancelReservationResponseDtoV2 of(UUID reservationId) {
        return new CancelReservationResponseDtoV2(
                reservationId
        );
    }
}
