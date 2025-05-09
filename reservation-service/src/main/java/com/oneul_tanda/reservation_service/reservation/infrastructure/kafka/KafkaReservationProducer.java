package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka;

import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReservationProducer {

    private final KafkaTemplate<String, ReservationCanceledEvent> kafkaTemplate;

    /**
     * 예약 취소 요청 이벤트를 Kafka 토픽에 발행
     */
    public void sendReservationCanceledEvent(UUID reservationId, UUID flightId, UUID userId, int seatCount) {

        ReservationCanceledEvent event = ReservationCanceledEvent.of(
                reservationId,
                flightId,
                seatCount,
                "reservation-canceled");

        kafkaTemplate.send("reservation-canceled", reservationId.toString(), event);
    }

}
