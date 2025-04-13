package com.oneul_tanda.reservation_service.reservation.presentation;

import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update.ConfirmReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
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
    public ResponseEntity<CreateReservationResponseDto> createReservation(@RequestBody CreateReservationRequestDto requestDto) {
        return ResponseEntity.ok(reservationService.createReservation(requestDto));
    }


    /**
     * 에약 확정 (예약 수정)
     */
    @PutMapping("/{reservationId}/confirm")
    public ResponseEntity<ConfirmReservationResponseDto> confirmReservation(@PathVariable UUID reservationId,
                                                                            @RequestBody ConfirmReservationRequestDto requestDto) {
        return ResponseEntity.ok(reservationService.confirmReservation(ConfirmReservationCommand.of(reservationId, requestDto)));
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
}
