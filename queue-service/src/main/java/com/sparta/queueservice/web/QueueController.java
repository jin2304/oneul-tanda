package com.sparta.queueservice.web;

import com.sparta.queueservice.application.dto.FlightRequestDto;
import com.sparta.queueservice.application.dto.QueueResponseDto;
import com.sparta.queueservice.application.service.QueueServiceV1;
import com.sparta.queueservice.application.service.QueueServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueController {
    private final QueueServiceV1 queueServiceV1;
    private final QueueServiceV2 queueServiceV2;

    @PostMapping("/v1/queue")
    public ResponseEntity<QueueResponseDto> tryReserveV1(@RequestBody FlightRequestDto request,
                                     @RequestHeader("X-User-ID") UUID userId) {

        QueueResponseDto response = queueServiceV1.tryReserve(request, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v2/queue")
    public ResponseEntity<QueueResponseDto> tryReserveV2(@RequestBody FlightRequestDto request,
                                                         @RequestHeader("X-User-ID") UUID userId) {

        QueueResponseDto response = queueServiceV2.tryReserve(request, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
