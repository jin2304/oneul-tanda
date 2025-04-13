package com.oneul_tanda.flight_service.presentation.dtos.airport;

import com.oneul_tanda.flight_service.application.dtos.airport.CreateAirportCommand;
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

    public CreateAirportCommand toCommand() {
        return CreateAirportCommand.of(
                this.code,
                this.name,
                this.city,
                this.country
        );
    }
}
