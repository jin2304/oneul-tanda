package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV2;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateReservationCommand;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReservationService {

    CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command);

    void createHoldReservationV2(CreateHoldReservationCommand command);

    CreateReservationResponseDto createReservation(CreateReservationCommand command);

    ReadReservationResponseDto readReservation(UUID reservationId);

    Page<ReadReservationResponseDto> readAllReservation(Pageable pageable);

    ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand command);

    CancelReservationResponseDto cancelReservation(UUID reservationId);
}
