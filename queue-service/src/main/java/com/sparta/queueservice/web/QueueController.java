package com.sparta.queueservice.web;

import com.sparta.queueservice.application.dto.FlightRequestDto;
import com.sparta.queueservice.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void tryReserve(@RequestBody FlightRequestDto request,
                           @RequestHeader("X-User-ID") String userId) {
        queueService.tryReserve(request, userId);
    }
}
