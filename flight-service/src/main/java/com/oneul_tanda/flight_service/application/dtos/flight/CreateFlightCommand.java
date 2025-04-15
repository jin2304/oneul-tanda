package com.oneul_tanda.flight_service.application.dtos.flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CreateFlightCommand {

    private String flightNum;
    private String airlineCode;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private BigDecimal price;
    private int remainingSeats;

    public static CreateFlightCommand of(
            String flightNum, String airline,
            String departureAirport, String arrivalAirport,
            LocalDateTime departureDate, LocalDateTime arrivalDate,
            BigDecimal price, int remainingSeats
    ) {
        return CreateFlightCommand.builder()
                .flightNum(flightNum)
                .airlineCode(airline)
                .departureAirportCode(departureAirport)
                .arrivalAirportCode(arrivalAirport)
                .departureDate(departureDate)
                .arrivalDate(arrivalDate)
                .price(price)
                .remainingSeats(remainingSeats)
                .build();
    }
}
