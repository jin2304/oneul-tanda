package com.oneul_tanda.reservation_service.reservation.presentation;

import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV2;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.create.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDtoV2;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "reservation healthcheck";
    }


    /**
     * 에약 생성
     */
    @PostMapping
    public ResponseEntity<CreateReservationResponseDto> createReservation(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody @Valid CreateReservationRequestDto requestDto
    ) {
        return ResponseEntity.ok(reservationService.createReservation(CreateReservationCommand.of(userId, requestDto)));
    }


    /**
     * 에약 확정 V1 (RDB 기반)
     */
    @PutMapping("/{reservationId}/confirm")
    public ResponseEntity<ConfirmReservationResponseDto> confirmReservation(
            @RequestHeader("X-User-ID") UUID userId,
            @PathVariable UUID reservationId,
            @RequestBody @Valid ConfirmReservationRequestDto requestDto) {
        return ResponseEntity.ok(reservationService.confirmReservation(ConfirmReservationCommand.of(userId, reservationId, requestDto)));
    }



    /**
     * 예약 확정 v2 (Redis 기반)
     */
    @PutMapping("/confirm")
    public ResponseEntity<ConfirmReservationResponseDto> confirmReservationV2(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody @Valid ConfirmReservationRequestDtoV2 requestDto) {
        return ResponseEntity.ok(reservationService.confirmReservationV2(ConfirmReservationCommandV2.of(userId, requestDto)));
    }


    /**
     * 예약 단일 조회
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReadReservationResponseDto> readReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.readReservation(reservationId));
    }


    /**
     * 예약 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<ReadReservationResponseDto>> readAllReservation(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reservationService.readAllReservation(pageable));
    }



    /**
     * 예약 취소(예약 수정)
     */
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<CancelReservationResponseDto> cancelReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }
}
