package com.oneul_tanda.reservation_service.ticket.domain.entity;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;
import org.bouncycastle.pqc.jcajce.provider.BIKE;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "m_tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "flight_id", nullable = false)
    private UUID flightId;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false)
    private SeatClass seatClass;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;
    
}
