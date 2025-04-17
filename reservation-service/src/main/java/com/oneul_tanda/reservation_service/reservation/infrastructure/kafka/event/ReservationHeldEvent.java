package com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event;


import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationHeldEvent(
        String eventId,                    // 대상: 이벤트 고유 ID or 이벤트 대상 ID
        String eventType,                  // 행위: 이벤트 타입    (ex: 예약 선점)
        LocalDateTime occurrenceDateTime,  // 시간: 행위 발생 시각  (ex: 선점이 일어난 시간)
        Data data                          // 정보: 행위와 관련된 정보
) {

    public record Data(
            UUID flightId,
            UUID userId,
            int seatCount
    ) {}
}

