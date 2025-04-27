package com.sparta.queueservice.web;

import com.sparta.queueservice.application.dto.FlightRequestDto;
import com.sparta.queueservice.application.dto.QueueResponseDto;
import com.sparta.queueservice.application.service.QueueService;
import com.sparta.queueservice.infrastructure.kafka.event.EventStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping
    public ResponseEntity<QueueResponseDto> tryReserve(@RequestBody FlightRequestDto request,
                                     @RequestHeader("X-User-ID") UUID userId) {

        QueueResponseDto response = queueService.tryReserve(request, userId);
        HttpStatus status = response.getStatus() == EventStatusEnum.SUCCESS
                ? HttpStatus.NO_CONTENT
                : HttpStatus.CONFLICT;
        return new ResponseEntity<>(response, status);
    }
}
