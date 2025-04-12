package com.oneul_tanda.flight_service.presentation.dtos.airline;

import com.oneul_tanda.flight_service.domain.entity.Airline;
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
public class AirlineResponse {

    private UUID id;
    private String code;
    private String name;

    public static AirlineResponse from(Airline airline) {
        return AirlineResponse.builder()
                .id(airline.getId())
                .code(airline.getCode())
                .name(airline.getName())
                .build();
    }
}
