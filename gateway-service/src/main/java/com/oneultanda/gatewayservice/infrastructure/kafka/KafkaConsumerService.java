package com.oneultanda.gatewayservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${tokenVersion-expiration}")
    private int toKenVersionExpiration;

    @KafkaListener(topics = KafkaTopics.TOKEN_VERSION_CHANGED, groupId = "gateway_group")
    public void handleDeliveryStatusChanged(TokenVersionChangedEvent event) {
        log.info("토큰 변경 이벤트 수신: {}", event);

        try {
            // redis 처리
            log.info("{}의 토큰버전이 {}로 변경. redis 처리가 필요합니다!", event.userId(), event.tokenVersion());
            String key = "token_version:" + event.userId();
            redisTemplate.opsForValue().set(key, String.valueOf(event.tokenVersion()), Duration.ofMinutes(toKenVersionExpiration));

        } catch(Exception e) {
            // todo: 실패에 대한 보상처리 과정
            log.error("토큰 만료 처리 실패", e.getMessage(), e);
        }
    }
}