package com.oneul_tanda.reservation_service.reservation.application.command;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.create.CreateReservationRequestDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateReservationCommand(
        UUID userId,
        UUID flightId,
        int seatCount,
        List<CreatePassengerCommand> passengers
) {


    public static CreateReservationCommand of(UUID userId, CreateReservationRequestDto dto) {
        return new CreateReservationCommand(
                userId,
                dto.flightId(),
                dto.seatCount(),
                dto.passengers().stream()
                        .map(CreatePassengerCommand::from)
                        .collect(Collectors.toList())
        );
    }

    public record CreatePassengerCommand(
            String name,
            String birth,
            Gender gender,
            String passportNumber
    ) {
        public static CreatePassengerCommand from(CreateReservationRequestDto.CreatePassengerDto dto) {
            return new CreatePassengerCommand(
                    dto.name(),
                    dto.birth(),
                    dto.gender(),
                    dto.passportNumber());
        }
    }
}


