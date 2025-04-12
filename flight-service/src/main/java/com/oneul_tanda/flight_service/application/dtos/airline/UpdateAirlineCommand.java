package com.oneul_tanda.flight_service.application.dtos.airline;

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
public class UpdateAirlineCommand {

    private UUID airlineId;
    private String code;
    private String name;

    public static UpdateAirlineCommand of(
            UUID airlineId,
            String code,
            String name
    ) {
        return UpdateAirlineCommand.builder()
                .airlineId(airlineId)
                .code(code)
                .name(name)
                .build();
    }
}
