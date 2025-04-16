package com.oneul_tanda.flight_service.presentation.dtos.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
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
public class FlightResponse {

    private UUID id;
    private String flightNum;
    private String airlineCode;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private BigDecimal price;
    private Integer remainingSeats;

    public static FlightResponse from(
            FlightEntity flight
    ) {
        return FlightResponse.builder()
                .id(flight.getId())
                .flightNum(flight.getFlightNum())
                .airlineCode(flight.getAirline().getCode())
                .departureAirportCode(flight.getDepartureAirport().getCode())
                .arrivalAirportCode(flight.getArrivalAirport().getCode())
                .departureDate(flight.getDepartureDate())
                .arrivalDate(flight.getArrivalDate())
                .price(flight.getPrice())
                .remainingSeats(flight.getRemainingSeats())
                .build();
    }

    // Amadeus API에서 받은 항공편 정보로 FlightResponse 생성
    public static FlightResponse from(
            String flightNum,
            String airlineCode,
            String departureAirportCode,
            String arrivalAirportCode,
            LocalDateTime departureDate,
            LocalDateTime arrivalDate,
            BigDecimal price,
            Integer remainingSeats
    ) {
        return FlightResponse.builder()
                .id(UUID.randomUUID()) // 응답 객체 생성시 임시 UUID 부여
                .flightNum(flightNum)
                .airlineCode(airlineCode)
                .departureAirportCode(departureAirportCode)
                .arrivalAirportCode(arrivalAirportCode)
                .departureDate(departureDate)
                .arrivalDate(arrivalDate)
                .price(price.setScale(2, BigDecimal.ROUND_HALF_UP)) // 소수점 2자리로 반올림
                .remainingSeats(remainingSeats)
                .build();
    }
}
