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
        private EventStatusEnum status;
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
                                                              EventStatusEnum status) {
        return ReservationHeldEvent.builder()
                .eventId(UUID.randomUUID())
                .status(status)
                .reservationTime(LocalDateTime.now())
                .data(ReservationHeldEvent.Data.builder()
                        .flightId(flightId)
                        .userId(userId)
                        .seatCount(seatCount)
                        .build())
                .build();
    }
}
