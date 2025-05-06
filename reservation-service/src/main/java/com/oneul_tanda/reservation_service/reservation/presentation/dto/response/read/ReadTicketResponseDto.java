package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Ticket;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ReadTicketResponseDto(
        UUID ticketId,
        UUID flightId,
        String seatClass,
        BigDecimal unitPrice,
        ReadPassengerResponseDto passenger
) {

    // Entity -> DTO 변환 메서드
    public static ReadTicketResponseDto from(Ticket ticket) {
        return ReadTicketResponseDto.builder()
                .ticketId(ticket.getId())
                .flightId(ticket.getFlightId())
                .seatClass(ticket.getSeatClass().name())
                .unitPrice(ticket.getUnitPrice())
                .passenger(ticket.getPassenger() != null ? ReadPassengerResponseDto.from(ticket.getPassenger()) : null)
                .build();
    }
}

