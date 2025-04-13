package com.oneul_tanda.flight_service.presentation.dtos.airport;

import com.oneul_tanda.flight_service.domain.entity.Airport;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AirportResponse {

    private UUID id;
    private String code;
    private String name;
    private String country;
    private String city;

    public static AirportResponse from(Airport airport) {
        return AirportResponse.builder()
                .id(airport.getId())
                .code(airport.getCode())
                .name(airport.getName())
                .country(airport.getCountry())
                .city(airport.getCity())
                .build();
    }
}
