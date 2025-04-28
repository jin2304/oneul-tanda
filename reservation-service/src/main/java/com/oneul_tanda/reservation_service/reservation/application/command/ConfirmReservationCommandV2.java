package com.oneul_tanda.reservation_service.reservation.application.command;

import com.oneul_tanda.reservation_service.reservation.application.dto.PassengerDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDtoV2;

import java.util.List;
import java.util.UUID;

public record ConfirmReservationCommandV2(
        UUID userId,
        UUID flightId,
        List<PassengerDto> passengers
) {
    public static ConfirmReservationCommandV2 of(UUID userId, ConfirmReservationRequestDtoV2 dto) {
        return new ConfirmReservationCommandV2(
                userId,
                dto.flightId(),
                dto.passengers() == null ? List.of() : dto.passengers().stream()
                        .map(PassengerDto::from)
                        .toList()
        );
    }
}
