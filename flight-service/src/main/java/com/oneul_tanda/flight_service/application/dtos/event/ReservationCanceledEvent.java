package com.oneul_tanda.flight_service.application.dtos.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCanceledEvent {
    private UUID eventId;                   // 대상: 이벤트 고유 ID or 이벤트 대상 ID
    private String eventType;               // 행위: 이벤트 타입    (ex: 예약 선점)
    private LocalDateTime reservationCanceledTime;  // 시간: 행위 발생 시각  (ex: 선점이 일어난 시간)
    private Data data;                      // 정보: 행위와 관련된 정보

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Data {
        private UUID flightId;
        private Integer seatCount;
    }

    public static ReservationCanceledEvent of(UUID reservationId,
                                              UUID flightId,
                                              Integer seatCount,
                                              String eventType
    ) {
        return ReservationCanceledEvent.builder()
                .eventId(reservationId)
                .eventType(eventType)
                .reservationCanceledTime(LocalDateTime.now())
                .data(ReservationCanceledEvent.Data.builder()
                        .flightId(flightId)
                        .seatCount(seatCount)
                        .build())
                .build();
    }
}