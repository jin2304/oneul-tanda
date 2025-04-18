package com.oneultanda.userservice.infrastructure.kafka;

import java.util.UUID;

public record TokenVersionChangedEvent(
        UUID userId,
        int tokenVersion
) {
    public static TokenVersionChangedEvent of(UUID userId, int tokenVersion) {
        return new TokenVersionChangedEvent(userId, tokenVersion);
    }
}
