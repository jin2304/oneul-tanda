package com.oneul_tanda.flight_service.infrastructure.event;

import com.oneul_tanda.flight_service.application.event.FlightEventKafkaProducer;
import com.oneul_tanda.flight_service.application.dtos.event.ReservationCanceledEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightEventKafkaProducerImpl implements FlightEventKafkaProducer {

    private final KafkaTemplate<String, ReservationCanceledEvent> kafkaTemplate;

    public void sendFlightSeatRecovered(UUID reservationId, UUID flightId, Integer seatCount) {
        ReservationCanceledEvent event = ReservationCanceledEvent
                .of(reservationId, flightId, seatCount, "flight-seatRecovered");
        kafkaTemplate.send("flight-seatRecovered", reservationId.toString(), event);

        log.info("Sending ReservationCanceledEvent to Kafka: {}", event);
    }
}
