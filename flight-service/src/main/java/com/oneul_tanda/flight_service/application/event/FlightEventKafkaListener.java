package com.oneul_tanda.flight_service.application.event;

public interface FlightEventKafkaListener {

    void consumePaymentCanceledFromPayment(ReservationCanceledEvent event);
}
