package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Ticket;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ConfirmTicketResponseDto(
        UUID ticketId,
        UUID flightId,
        String seatClass,
        BigDecimal unitPrice,
        ConfirmPassengerResponseDto passenger
) {

    // Entity -> DTO 변환 메서드
    public static ConfirmTicketResponseDto from(Ticket ticket) {
        return ConfirmTicketResponseDto.builder()
                .ticketId(ticket.getId())
                .flightId(ticket.getFlightId())
                .seatClass(ticket.getSeatClass().name())
                .unitPrice(ticket.getUnitPrice())
                .passenger(ConfirmPassengerResponseDto.from(ticket.getPassenger()))
                .build();
    }
}

