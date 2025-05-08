package com.sparta.paymentservice.infrastructure.kafka;

import com.sparta.paymentservice.infrastructure.kafka.event.ReservationCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducerService {
    private  final KafkaTemplate<String, ReservationCanceledEvent> kafkaTemplate;

    public void sendPaymentCanceled(UUID reservationId, UUID flightId, int seatCount) {
        ReservationCanceledEvent event = ReservationCanceledEvent
                .createReservationCanceledEvent(reservationId, flightId, seatCount, "payment-canceled");

        kafkaTemplate.send("payment-canceled", reservationId.toString(), event);
    }
}
