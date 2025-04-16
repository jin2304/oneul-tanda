package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Gender;
import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;

import java.util.UUID;

public record CreatePassengerResponseDto(
        UUID passengerId,
        String name,
        String birth,
        Gender gender,
        String passportNumber
) {

    // Entity -> DTO 변환 메서드
    public static CreatePassengerResponseDto from(Passenger passenger) {
        return new CreatePassengerResponseDto(
                passenger.getId(),
                passenger.getName(),
                passenger.getBirth(),
                passenger.getGender(),
                passenger.getPassportNumber()
        );
    }
}
