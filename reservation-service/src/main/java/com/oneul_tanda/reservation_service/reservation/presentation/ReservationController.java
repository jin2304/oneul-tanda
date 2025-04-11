package com.oneul_tanda.reservation_service.reservation.presentation;

import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.CreateReservationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
