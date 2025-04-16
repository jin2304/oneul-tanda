package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka;

import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationHeldEvent;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationHeldEventConsumer {

    private final ReservationService reservationService;


    @KafkaListener(
            topics = "reservation-held",
            groupId = "reservation-service",
            containerFactory = "reservationHeldListenerFactory"
    )
    public CreateHoldReservationResponseDto consumeReservationHeldForReservation(ReservationHeldEvent event) {

        var data = event.data();

        CreateHoldReservationCommand command = CreateHoldReservationCommand.of(
                data.flightId(),
                data.userId(),
                data.seatCount()
        );

        // 예약 임시 생성(사용자가 추후 예약정보 입력하여 예약 완료)
        return reservationService.createHoldReservation(command);
    }

}
