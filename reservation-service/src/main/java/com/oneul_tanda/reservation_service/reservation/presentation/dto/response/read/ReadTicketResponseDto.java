package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read;

import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreatePassengerResponseDto;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ReadTicketResponseDto(
        UUID ticketId,
        UUID flightId,
        String seatClass,
        BigDecimal unitPrice,
        CreatePassengerResponseDto passenger
) {

    // Entity -> DTO 변환 메서드
    public static ReadTicketResponseDto from(Ticket ticket) {
        return ReadTicketResponseDto.builder()
                .ticketId(ticket.getId())
                .flightId(ticket.getFlightId())
                .seatClass(ticket.getSeatClass().name())
                .unitPrice(ticket.getUnitPrice())
                .passenger(CreatePassengerResponseDto.from(ticket.getPassenger()))
                .build();
    }
}

