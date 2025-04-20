package com.oneul_tanda.flight_service.presentation.dtos.airport;

import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
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
    private String city;
    private String country;

    public static AirportResponse from(AirportEntity airport) {
        return AirportResponse.builder()
                .id(airport.getId())
                .code(airport.getCode())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .build();
    }
}
