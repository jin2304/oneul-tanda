package com.oneul_tanda.flight_service.presentation.dtos;

import com.oneul_tanda.flight_service.application.dtos.AirportCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAirportRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    public AirportCommand toCommand() {
        return new AirportCommand(code, name, city, country);
    }
}
