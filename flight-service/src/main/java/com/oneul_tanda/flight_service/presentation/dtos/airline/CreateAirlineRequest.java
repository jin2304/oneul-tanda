package com.oneul_tanda.flight_service.presentation.dtos.airline;

import com.oneul_tanda.flight_service.application.dtos.airline.CreateAirlineCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateAirlineRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    public CreateAirlineCommand toCommand() {
        return CreateAirlineCommand.of(
                this.code,
                this.name
        );
    }
}
