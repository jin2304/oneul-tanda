package com.oneul_tanda.flight_service.presentation.dtos.airport;

import com.oneul_tanda.flight_service.application.dtos.airport.UpdateAirportCommand;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateAirportRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    public UpdateAirportCommand toCommand(UUID airportId) {
        return UpdateAirportCommand.of(
                airportId,
                this.code,
                this.name,
                this.city,
                this.country
        );
    }
}
