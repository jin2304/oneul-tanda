package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.ReservationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record CreateHoldReservationResponseDtoV1(
        UUID reservationId,
        UUID userId,
        BigDecimal totalPrice,
        ReservationStatus status,
        List<CreateHoldTicketResponseDto> tickets,
        boolean success,
        String message
) implements CreateHoldReservationResponseDto {

    public static CreateHoldReservationResponseDtoV1 Success(Reservation reservation) {
        return CreateHoldReservationResponseDtoV1.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUserId())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .tickets(reservation.getTicketList().stream()
                        .map(CreateHoldTicketResponseDto::from)
                        .collect(Collectors.toList()))
                .success(true)
                .message("예약 임시 생성 완료")
                .build();
    }


    public static CreateHoldReservationResponseDtoV2 Failed(){
        return CreateHoldReservationResponseDtoV2.builder()
                .success(false)
                .message("예약 임시 생성 실패")
                .build();
    }
}
