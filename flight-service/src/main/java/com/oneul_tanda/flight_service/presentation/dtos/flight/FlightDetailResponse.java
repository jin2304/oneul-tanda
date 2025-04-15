package com.oneul_tanda.flight_service.presentation.dtos.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import java.math.BigDecimal;
import java.time.Duration;
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
public class FlightDetailResponse {
    private UUID id;
    private String flightNum;
    private String airlineCode;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private String  formattedDuration;
    private BigDecimal price;
    private int remainingSeats;

    public static FlightDetailResponse from(
            FlightEntity flight
    ) {
        return FlightDetailResponse.builder()
                .id(flight.getId())
                .flightNum(flight.getFlightNum())
                .airlineCode(flight.getAirline().getCode())
                .departureAirportCode(flight.getDepartureAirport().getCode())
                .arrivalAirportCode(flight.getArrivalAirport().getCode())
                .departureDate(flight.getDepartureDate())
                .arrivalDate(flight.getArrivalDate())
                .formattedDuration(getFormattedDuration(flight.getDuration()))
                .price(flight.getPrice())
                .remainingSeats(flight.getRemainingSeats())
                .build();
    }

    public static String getFormattedDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + "시간 " + minutes + "분";
    }
}
