package com.sparta.queueservice.infrastructure.Kafka;

import com.sparta.queueservice.infrastructure.Kafka.event.EventStatusEnum;
import com.sparta.queueservice.infrastructure.Kafka.event.ReservationHeldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, ReservationHeldEvent> kafkaTemplate;

    // 대기열 선점 성공시 성공 메시지 전달
    public void sendReserveSuccess(UUID flightId, String userId, int seatCount, EventStatusEnum status) {
        ReservationHeldEvent event = ReservationHeldEvent
                .createReservationEvent(flightId, userId, seatCount, status);

        kafkaTemplate.send("reservation-held", flightId.toString(), event);
    }
    // 대기열 선점 실패시 실패 메세지 전달
    public void sendReserveFailed(UUID flightId, String userId, int seatCount,  EventStatusEnum status) {
        ReservationHeldEvent event = ReservationHeldEvent
                .createReservationEvent(flightId, userId, seatCount, status);

        kafkaTemplate.send("reservation-failed", flightId.toString(), event);
    }
}
