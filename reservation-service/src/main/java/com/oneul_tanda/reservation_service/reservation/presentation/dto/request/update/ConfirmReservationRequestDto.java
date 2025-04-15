package com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;

import java.util.List;
import java.util.UUID;

public record ConfirmReservationRequestDto(
        List<ConfirmTicketDto> tickets
) {

    public record ConfirmTicketDto(
            UUID ticketId,
            ConfirmPassengerDto passenger
    ) {}

    public record ConfirmPassengerDto(
            String name,
            String birth,
            Gender gender,
            String passportNumber
    ) {}
}

