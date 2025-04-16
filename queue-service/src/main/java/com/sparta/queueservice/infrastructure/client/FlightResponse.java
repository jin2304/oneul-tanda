package com.sparta.queueservice.infrastructure.client;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FlightResponse {
    private UUID flightId;
    private Integer remainingSeats;
}
