package com.oneul_tanda.flight_service.infrastructure.event;

import com.oneul_tanda.flight_service.application.event.FlightEventKafkaListener;
import com.oneul_tanda.flight_service.application.event.ReservationCanceledEvent;
import com.oneul_tanda.flight_service.application.service.flight.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightEventKafkaListenerImpl implements FlightEventKafkaListener {

    private final FlightService flightService;

    // 예약 취소 이벤트 수신
    @KafkaListener(
            topics = "payment-canceled",
            groupId = "flight-service",
            containerFactory = "paymentCanceledListenerFactory"
    )
    public void consumePaymentCanceledFromPayment(ReservationCanceledEvent event) {
        log.info("Received ReservationCanceledEvent From Payment: {}", event);
        ReservationCanceledEvent.Data data = event.getData();
        // 항공편 예약 취소(결제 취소)시 좌석 수 증가
        flightService.increaseSeatsV2(event.getEventId(), data.getFlightId(), data.getSeatCount());
    }
}
