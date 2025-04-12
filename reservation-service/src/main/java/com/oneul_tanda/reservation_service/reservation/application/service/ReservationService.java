package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReservationService {

    CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command);

    CreateReservationResponseDto createReservation(CreateReservationRequestDto requestDto);

    ReadReservationResponseDto readReservation(UUID reservationId);

    Page<ReadReservationResponseDto> readAllReservation(Pageable pageable);
}
