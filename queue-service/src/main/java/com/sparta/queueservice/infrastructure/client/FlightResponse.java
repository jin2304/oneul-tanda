package com.sparta.queueservice.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightResponse {
    private UUID id;
    private Integer remainingSeats;
}
