package com.oneul_tanda.flight_service.presentation.dtos.airline;

import com.amadeus.resources.Airline;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AirlineSearchResponse {

    private String code;
    private String name;

    public static AirlineSearchResponse from(Airline airline) {
        return AirlineSearchResponse.builder()
                .code(airline.getIataCode())
                .name(airline.getCommonName())
                .build();
    }
}
