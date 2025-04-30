package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHeldEvent {
    private UUID eventId;                   // 대상: 이벤트 고유 ID or 이벤트 대상 ID
    private String eventType;               // 행위: 이벤트 타입    (ex: 예약 선점)
    private LocalDateTime reservationTime;  // 시간: 행위 발생 시각  (ex: 선점이 일어난 시간)
    private Data data;                      // 정보: 행위와 관련된 정보

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private UUID flightId;
        private UUID userId;
        private Integer seatCount;
    }
}

