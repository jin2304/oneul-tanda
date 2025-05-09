package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka;

import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.service.ReservationService;
import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationCanceledEvent;
import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationHeldEvent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationHeldEventConsumer {

    private final ReservationService reservationService;


    @KafkaListener(
            topics = "reservation-held",
            groupId = "reservation-service",
            containerFactory = "reservationHeldListenerFactory"
    )
    public void consumeReservationHeldForReservation(ReservationHeldEvent event) {

        try{
            ReservationHeldEvent.Data data = event.getData();

            CreateHoldReservationCommand command = CreateHoldReservationCommand.of(
                    data.getFlightId(),
                    data.getUserId(),
                    data.getSeatCount()
            );

            // 예약 임시 생성(사용자가 추후 예약정보 입력하여 예약 완료)
            reservationService.createHoldReservation(command);

        } catch (FeignException e) {
            log.error("항공편 조회 실패 - flightId={}, error={}", event.getData().getFlightId(), e.getMessage());
            throw e; // DLQ 또는 재시도 유도됨

        } catch (Exception e) {
            log.error("임시 예약 생성 실패: {}, {}", event, e.getMessage(), e);
            throw e; // DLQ 또는 재시도 유도됨
        }


    }




    @KafkaListener(
            topics = "flight-seatRecovered",
            groupId = "reservation-service",
            containerFactory = "reservationCanceledListenerFactory"
    )
    public void handleSeatRecovered(ReservationCanceledEvent event) {
        reservationService.cancelReservationConfirm(event.getEventId());
    }
}
