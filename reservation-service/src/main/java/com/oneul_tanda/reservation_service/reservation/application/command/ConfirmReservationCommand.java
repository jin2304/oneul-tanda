package com.oneul_tanda.reservation_service.reservation.application.command;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDto;

import java.util.List;

import java.util.UUID;

public record ConfirmReservationCommand(
        UUID userId,
        UUID reservationId,
        List<ConfirmTicketCommand> tickets
) {
    public static ConfirmReservationCommand of(UUID userId, UUID reservationId, ConfirmReservationRequestDto dto) {
        return new ConfirmReservationCommand(
                userId,
                reservationId,
                dto.tickets().stream()
                        .map(ConfirmTicketCommand::from)
                        .toList()
        );
    }



    public record ConfirmTicketCommand(
            UUID ticketId,
            ConfirmPassengerCommand passenger
    ) {
        public static ConfirmTicketCommand from(ConfirmReservationRequestDto.ConfirmTicketDto dto) {
            return new ConfirmTicketCommand(dto.ticketId(), ConfirmPassengerCommand.from(dto.passenger()));
        }
    }



    public record ConfirmPassengerCommand(
            String name,
            String birth,
            Gender gender,
            String passportNumber
    ) {
        public static ConfirmPassengerCommand from(ConfirmReservationRequestDto.ConfirmPassengerDto dto) {
            return new ConfirmPassengerCommand(dto.name(), dto.birth(), dto.gender(), dto.passportNumber());
        }
    }
}


