package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.ReservationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record CreateHoldReservationResponseDto(
        UUID reservationId,
        Long userId,
        BigDecimal totalPrice,
        ReservationStatus status,
        List<CreateHoldTicketResponseDto> tickets
) {

    // Entity -> DTO 변환 메서드
    public static CreateHoldReservationResponseDto from(Reservation reservation) {
        return CreateHoldReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUserId())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .tickets(reservation.getTicketList().stream()
                        .map(CreateHoldTicketResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
