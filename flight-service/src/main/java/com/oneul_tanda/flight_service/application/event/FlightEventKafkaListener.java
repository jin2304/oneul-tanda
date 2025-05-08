package com.oneul_tanda.flight_service.application.event;

import com.oneul_tanda.flight_service.application.dtos.event.ReservationCanceledEvent;

public interface FlightEventKafkaListener {

    void consumePaymentCanceledFromPayment(ReservationCanceledEvent event);
}
