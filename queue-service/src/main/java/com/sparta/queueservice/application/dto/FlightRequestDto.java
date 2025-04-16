package com.sparta.queueservice.application.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FlightRequestDto {
    private UUID flightId;
    private Integer seatCount;
}
