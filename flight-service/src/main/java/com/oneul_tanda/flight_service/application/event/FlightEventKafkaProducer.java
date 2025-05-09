package com.oneul_tanda.flight_service.application.event;

import java.util.UUID;

public interface FlightEventKafkaProducer {

    void sendFlightSeatRecovered(UUID reservationId, UUID flightId, Integer seatCount);
}
