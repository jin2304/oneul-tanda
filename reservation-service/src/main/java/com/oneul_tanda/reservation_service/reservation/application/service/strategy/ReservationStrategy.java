package com.oneul_tanda.reservation_service.reservation.application.service.strategy;

import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;

import java.util.UUID;

public interface ReservationStrategy {

    CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command);

    ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand command);

    CancelReservationResponseDto cancelReservation(UUID reservationId);
}
