package com.oneul_tanda.flight_service.application.dtos.airline;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateAirlineCommand {

    private String code;
    private String name;

    public static CreateAirlineCommand of(
            String code, String name
    ) {
        return CreateAirlineCommand.builder()
                .code(code)
                .name(name)
                .build();
    }
}
