package com.sparta.queueservice.infrastructure.kafka;

import com.sparta.queueservice.infrastructure.kafka.event.EventStatusEnum;
import com.sparta.queueservice.infrastructure.kafka.event.ReservationHeldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, ReservationHeldEvent> kafkaTemplate;

    // 대기열 선점 성공시 성공 메시지 전달
    public void sendReserveSuccess(UUID flightId, UUID userId, Integer seatCount) {
        ReservationHeldEvent event = ReservationHeldEvent
                .createReservationEvent(flightId, userId, seatCount, "reservation-held");

        kafkaTemplate.send("reservation-held", flightId.toString(), event);
    }
}
