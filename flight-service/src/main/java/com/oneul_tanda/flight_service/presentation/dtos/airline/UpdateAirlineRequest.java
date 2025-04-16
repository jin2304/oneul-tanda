package com.oneul_tanda.flight_service.presentation.dtos.airline;

import com.oneul_tanda.flight_service.application.dtos.airline.UpdateAirlineCommand;
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
public class UpdateAirlineRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    public UpdateAirlineCommand toCommand(UUID airlineId) {
        return UpdateAirlineCommand.of(
                airlineId,
                this.code,
                this.name
        );
    }
}
