package com.sparta.queueservice.infrastructure.kafka.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHeldEvent {
        private UUID eventId;
        private String eventType;
        private LocalDateTime reservationTime;
        private Data data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private UUID flightId;
        private UUID userId;
        private Integer seatCount;
    }

    public static ReservationHeldEvent createReservationEvent(UUID flightId,
                                                              UUID userId,
                                                              Integer seatCount,
                                                              String eventType) {
        return ReservationHeldEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .reservationTime(LocalDateTime.now())
                .data(ReservationHeldEvent.Data.builder()
                        .flightId(flightId)
                        .userId(userId)
                        .seatCount(seatCount)
                        .build())
                .build();
    }
}
