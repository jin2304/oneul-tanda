package com.oneul_tanda.flight_service.application.dtos.airport;

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
public class UpdateAirportCommand {

    private UUID airportId;
    private String code;
    private String name;
    private String city;
    private String country;

    public static UpdateAirportCommand of(
            UUID airportId,
            String code,
            String name,
            String city,
            String country
    ) {
        return UpdateAirportCommand.builder()
                .airportId(airportId)
                .code(code)
                .name(name)
                .city(city)
                .country(country)
                .build();
    }
}
