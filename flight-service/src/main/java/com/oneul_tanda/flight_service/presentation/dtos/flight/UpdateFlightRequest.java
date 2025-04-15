package com.oneul_tanda.flight_service.presentation.dtos.flight;

import com.oneul_tanda.flight_service.application.dtos.flight.UpdateFlightCommand;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class UpdateFlightRequest {

    private String flightNum;

    private String airlineCode;

    private String departureAirportCode;

    private String arrivalAirportCode;

    private LocalDateTime departureDate;

    private LocalDateTime arrivalDate;

    private BigDecimal price;

    private int remainingSeats;

    public UpdateFlightCommand toCommand(UUID flightId) {
        return UpdateFlightCommand.of(
                flightId,
                this.flightNum,
                this.airlineCode,
                this.departureAirportCode,
                this.arrivalAirportCode,
                this.departureDate,
                this.arrivalDate,
                this.price,
                this.remainingSeats
        );
    }
}
