package com.sparta.queueservice.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightRequestDto {
    private String flightId;
    private int seatCount;
}
