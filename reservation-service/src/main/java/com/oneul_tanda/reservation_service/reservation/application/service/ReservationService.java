package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.CreateReservationResponseDto;

public interface ReservationService {

    CreateReservationResponseDto createReservation(CreateReservationRequestDto requestDto);
}
