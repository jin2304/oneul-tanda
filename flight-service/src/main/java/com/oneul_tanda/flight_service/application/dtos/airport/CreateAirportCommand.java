package com.oneul_tanda.flight_service.application.dtos.airport;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateAirportCommand {

    private String code;
    private String name;
    private String city;
    private String country;

    public static CreateAirportCommand of(
            String code, String name, String city, String country
    ) {
        return CreateAirportCommand.builder()
                .code(code)
                .name(name)
                .city(city)
                .country(country)
                .build();
    }
}
