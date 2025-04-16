package com.oneul_tanda.flight_service.domain.entity;

import com.oneul_tanda.flight_service.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "m_flights")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class FlightEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "flight_num", nullable = false)
    private String flightNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id", nullable = false)
    private AirlineEntity airline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "arrival_date", nullable = false)
    private LocalDateTime arrivalDate;

    @Column(name = "duration", nullable = false)
    private Duration duration;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "remaining_seats", nullable = false)
    private Integer remainingSeats;

    public static FlightEntity from(String flightNum, AirlineEntity airlineCode, Airport departureAirport,
                                    Airport arrivalAirport,
                                    LocalDateTime departureDate, LocalDateTime arrivalDate,
                                    Duration duration, BigDecimal price,
                                    Integer remainingSeats
    ) {
        return FlightEntity.builder()
                .flightNum(flightNum)
                .airline(airlineCode)
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureDate(departureDate)
                .arrivalDate(arrivalDate)
                .duration(duration)
                .price(price)
                .remainingSeats(remainingSeats)
                .build();
    }

    public void updateOf(String flightNum, AirlineEntity airlineCode, Airport departureAirport,
                         Airport arrivalAirport,
                         LocalDateTime departureDate, LocalDateTime arrivalDate,
                         Duration duration, BigDecimal price, Integer remainingSeats
    ) {
        this.flightNum = flightNum;
        this.airline = airlineCode;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.duration = duration;
        this.price = price;
        this.remainingSeats = remainingSeats;
    }

    public void decreaseSeatCount(Integer requiredSeats) {
        if (requiredSeats == null || requiredSeats <= 0) {
            throw new IllegalArgumentException("Required seats must be positive");
        }
        if (this.remainingSeats < requiredSeats) {
            throw new IllegalStateException("Not enough seats");
        }
        this.remainingSeats -= requiredSeats;
    }

    public void increaseSeatCount(Integer requiredSeats) {
        if (requiredSeats == null || requiredSeats <= 0) {
            throw new IllegalArgumentException("Seats to add must be positive");
        }
        this.remainingSeats += requiredSeats;
    }
}
