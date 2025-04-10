package com.oneul_tanda.flight_service.presentation.dtos;

import com.oneul_tanda.flight_service.application.dtos.AirportCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AirportRequest {

    private String code;
    private String name;
    private String city;
    private String country;

    public AirportCommand toCommand() {
        return new AirportCommand(code, name, city, country);
    }
}
