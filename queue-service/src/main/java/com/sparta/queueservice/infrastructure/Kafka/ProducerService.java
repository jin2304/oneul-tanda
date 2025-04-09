package com.sparta.queueservice.infrastructure.Kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    // 대기열 선점 성공시 성공 메시지 전달
    public void sendReserveSuccess(String flightId, String userId, int seatCount) {
        kafkaTemplate.send("success", flightId + ":" + userId + ":" + seatCount);
    }
    // 대기열 선점 실패시 실패 메세지 전달
    public void sendReserveFailed(String flightId, String userId, int seatCount) {
        kafkaTemplate.send("failed", flightId + ":" + userId + ":" + seatCount);
    }
}
