package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event;

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
    private UUID eventId;                   // 대상: 이벤트 고유 ID or 이벤트 대상 ID
    private String eventType;               // 행위: 이벤트 타입
    private LocalDateTime reservationTime;  // 시간: 행위 발생 시각
    private Data data;                      // 정보: 행위와 관련된 정보

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private UUID flightId;
        private Integer seatCount;
    }


    public static ReservationCanceledEvent of(UUID reservationId,
                                              UUID flightId,
                                              Integer seatCount,
                                              String eventType) {

        return ReservationCanceledEvent.builder()
                .eventId(reservationId)
                .eventType(eventType)
                .reservationTime(LocalDateTime.now())
                .data(ReservationCanceledEvent.Data.builder()
                        .flightId(flightId)
                        .seatCount(seatCount)
                        .build())
                .build();
    }
}

