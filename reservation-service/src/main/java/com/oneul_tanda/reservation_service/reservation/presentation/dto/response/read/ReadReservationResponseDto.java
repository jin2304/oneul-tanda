package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.ReservationStatus;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateTicketResponseDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record ReadReservationResponseDto(
        UUID reservationId,
        UUID userId,
        BigDecimal totalPrice,
        ReservationStatus status,
        List<CreateTicketResponseDto> tickets
) {

    // Entity -> DTO 변환 메서드
    public static ReadReservationResponseDto from(Reservation reservation) {
        return ReadReservationResponseDto.builder()
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
