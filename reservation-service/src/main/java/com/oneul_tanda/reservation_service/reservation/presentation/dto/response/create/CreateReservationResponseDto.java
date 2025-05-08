package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.ReservationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record CreateReservationResponseDto(
        UUID reservationId,
        UUID userId,
        BigDecimal totalPrice,
        ReservationStatus status,
        List<CreateTicketResponseDto> tickets
) {

    // Entity -> DTO 변환 메서드
    public static CreateReservationResponseDto from(Reservation reservation) {
        return CreateReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUserId())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .tickets(reservation.getTicketList().stream()
                        .map(CreateTicketResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }




}
