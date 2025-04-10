package com.oneul_tanda.flight_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AirportCommand {

    private String code;
    private String name;
    private String city;
    private String country;
}
