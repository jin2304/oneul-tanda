package com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightDetailResponse {
    private UUID id;
    private String flightNum;
    private String airlineCode;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private String formattedDuration;
    private BigDecimal price;
    private Integer remainingSeats;

    public GetFlightInfo toInfo() {
        return new GetFlightInfo(
                id,
                flightNum,
                airlineCode,
                departureAirportCode,
                arrivalAirportCode,
                departureDate,
                arrivalDate,
                formattedDuration,
                price,
                remainingSeats
        );
    }
}

