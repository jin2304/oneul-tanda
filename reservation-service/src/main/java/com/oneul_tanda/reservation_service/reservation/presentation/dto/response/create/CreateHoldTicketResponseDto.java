package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CreateHoldTicketResponseDto(
        UUID ticketId,
        UUID flightId,
        String seatClass,
        BigDecimal unitPrice
) {

    // Entity -> DTO 변환 메서드
    public static CreateHoldTicketResponseDto from(Ticket ticket) {
        return CreateHoldTicketResponseDto.builder()
                .ticketId(ticket.getId())
                .flightId(ticket.getFlightId())
                .seatClass(ticket.getSeatClass().name())
                .unitPrice(ticket.getUnitPrice())
                .build();
    }
}

