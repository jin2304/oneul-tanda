package com.sparta.queueservice.application.dto;

import com.sparta.queueservice.infrastructure.kafka.event.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueResponseDto {
    private EventStatusEnum status;
    private String message;

    public static QueueResponseDto of(EventStatusEnum status, String message) {
        return QueueResponseDto.builder()
                .status(status)
                .message(message)
                .build();
    }
}
