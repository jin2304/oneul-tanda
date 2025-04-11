package com.oneul_tanda.reservation_service.reservation.presentation.dto.request;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;
import com.oneul_tanda.reservation_service.ticket.domain.entity.SeatClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateReservationRequestDto(Long userId,
                                          List<CreateTicketRequestDto> tickets)
{

    public record CreateTicketRequestDto(
            UUID flightId,
            SeatClass seatClass,
            BigDecimal price,
            CreatePassengerDto passenger
    ) {}

    public record CreatePassengerDto(
            String birth,
            Gender gender,
            String passportNumber
    ) {}
}
