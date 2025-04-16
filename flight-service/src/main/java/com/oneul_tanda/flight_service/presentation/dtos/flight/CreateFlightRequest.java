package com.oneul_tanda.flight_service.presentation.dtos.flight;

import com.oneul_tanda.flight_service.application.dtos.flight.CreateFlightCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateFlightRequest {

    private String flightNum;

    private String airlineCode;

    private String departureAirportCode;

    private String arrivalAirportCode;

    private LocalDateTime departureDate;

    private LocalDateTime arrivalDate;

    private BigDecimal price;

    private Integer remainingSeats;

    public CreateFlightCommand toCommand() {
        return CreateFlightCommand.of(
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
