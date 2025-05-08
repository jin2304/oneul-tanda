package com.sparta.paymentservice.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCanceledEvent {
    private UUID eventId;
    private String eventType;
    private LocalDateTime paymentCancelledTime;
    private Data data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private UUID flightId;
        private Integer seatCount;
    }

    public static ReservationCanceledEvent createReservationCanceledEvent(UUID reservationId,
                                                                          UUID flightId,
                                                                          Integer seatCount,
                                                                          String eventType) {
        return ReservationCanceledEvent.builder()
                .eventId(reservationId)
                .eventType(eventType)
                .paymentCancelledTime(LocalDateTime.now())
                .data((Data.builder()
                        .flightId(flightId)
                        .seatCount(seatCount)
                        .build()))
                .build();
    }
}
