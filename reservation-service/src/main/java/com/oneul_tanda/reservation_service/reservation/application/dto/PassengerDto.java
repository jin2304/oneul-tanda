package com.oneul_tanda.reservation_service.reservation.application.dto;

import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.Gender;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDtoV2;

public record PassengerDto(
        String name,
        String birth,
        Gender gender,
        String passportNumber
) {
    public static PassengerDto from(ConfirmReservationRequestDtoV2.ConfirmPassengerDtoV2 dto) {
        return new PassengerDto(
                dto.name(),
                dto.birth(),
                dto.gender(),
                dto.passportNumber()
        );
    }
}
