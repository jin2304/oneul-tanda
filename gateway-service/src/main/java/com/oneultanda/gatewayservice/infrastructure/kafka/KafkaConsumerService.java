package com.oneultanda.gatewayservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    @KafkaListener(topics = KafkaTopics.TOKEN_VERSION_CHANGED, groupId = "gateway_group")
    public void handleDeliveryStatusChanged(TokenVersionChangedEvent event) {
        log.info("토큰 변경 이벤트 수신: {}", event);

        try {
            // redis 처리
            log.info("{}의 토큰버전이 {}로 변경. redis 처리가 필요합니다!", event.userId(), event.tokenVersion());

        } catch(Exception e) {
            // todo: 실패에 대한 보상처리 과정
            log.error("토큰 만료 처리 실패", e.getMessage(), e);
        }
    }
}