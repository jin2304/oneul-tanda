package com.oneul_tanda.flight_service.presentation.dtos;

import com.amadeus.resources.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class AirportSearchResponse {

    private String code;
    private String name;
    private String city;
    private String country;

    public static AirportSearchResponse from(Location location) {
        return AirportSearchResponse.builder()
                .code(location.getIataCode())
                .name(location.getName())
                .city(location.getAddress().getCityName())
                .country(location.getAddress().getCountryName())
                .build();
    }
}
