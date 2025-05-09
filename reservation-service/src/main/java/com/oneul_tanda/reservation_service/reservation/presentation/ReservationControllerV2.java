package com.oneul_tanda.reservation_service.reservation.presentation;

import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV2;
import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDtoV2;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationControllerV2 {

    private final ReservationService reservationService;


    /**
     * 예약 확정 v2 (Redis 기반)
     */
    @PutMapping("/confirmV2")
    public ResponseEntity<ConfirmReservationResponseDto> confirmReservationV2(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid ConfirmReservationRequestDtoV2 requestDto) {
        System.out.println("requestDto: " + requestDto);
        return ResponseEntity.ok(reservationService.confirmReservation(ConfirmReservationCommandV2.of(userId, requestDto)));
    }



    /**
     * 예약 취소 V2
     */
    @PutMapping("/{reservationId}/cancelV2")
    public ResponseEntity<CancelReservationResponseDto> cancelReservationV2(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

}
