package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;
import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;

import java.util.UUID;

public record ReadPassengerResponseDto(
        UUID passengerId,
        String birth,
        Gender gender,
        String passportNumber
) {

    // Entity -> DTO 변환 메서드
    public static ReadPassengerResponseDto from(Passenger passenger) {
        return new ReadPassengerResponseDto(
                passenger.getId(),
                passenger.getBirth(),
                passenger.getGender(),
                passenger.getPassportNumber()
        );
    }
}
