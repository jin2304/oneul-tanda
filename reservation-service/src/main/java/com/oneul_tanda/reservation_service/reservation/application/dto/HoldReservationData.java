package com.oneul_tanda.reservation_service.reservation.application.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HoldReservationData(
        int seatCount,
        List<PassengerDto> passengers
) {

    public static HoldReservationData of(int seatCount, List<PassengerDto> passengers) {
        return HoldReservationData.builder()
                .seatCount(seatCount)
                .passengers(passengers)
                .build();
    }
}
