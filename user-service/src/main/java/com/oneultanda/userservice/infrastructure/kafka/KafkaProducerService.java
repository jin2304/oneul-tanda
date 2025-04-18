package com.oneultanda.userservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<UUID, TokenVersionChangedEvent> kafkaTemplate;

    public void sendTokenVersionChange(UUID userId, int tokenVersion) {
        TokenVersionChangedEvent event = TokenVersionChangedEvent.of(userId, tokenVersion);
        kafkaTemplate.send(KafkaTopics.TOKEN_VERSION_CHANGED, userId, event);
    }
}