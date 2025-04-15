package com.oneul_tanda.reservation_service.reservation.domain.entity;


import com.oneul_tanda.reservation_service.common.entity.BaseTimeEntity;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "m_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> ticketList;



    /**
     * 예약 생성
     */
    public static Reservation createReservation(UUID userId, List<Ticket> ticketList) {
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .ticketList(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO) // 임시 0원 설정
                .status(ReservationStatus.PENDING)
                .build();

        // 예약과(Reservation)과 티켓(Ticket) 간의 양방향 연관 관계 설정
        for (Ticket ticket : ticketList) {
            reservation.addTicket(ticket);
        }

        return reservation;
    }



    /**
     * 예약 임시 생성
     */
    public static Reservation createHoldReservation(UUID userId, List<Ticket> ticketList) {

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .ticketList(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO) // 임시 0원 설정
                .status(ReservationStatus.PENDING)
                .build();

        // 예약과(Reservation)과 티켓(Ticket) 간의 양방향 연관 관계 설정
        for (Ticket ticket : ticketList) {
            reservation.addTicket(ticket);
        }

        reservation.registerCreatedBy(userId);

        return reservation;
    }


    // 연관 관계 설정 메서드
    public void addTicket(Ticket ticket) {
        this.ticketList.add(ticket);   // 예약 티켓 리스트에 티켓 추가
        ticket.associateTicket(this);  // 티켓에서도 해당 예약 정보 설정, setter 대신 associateTicket 메서드 사용
    }




    /**
     * 예약 확정
     */
    public void confirmReservation() {
        this.status = ReservationStatus.RESERVED;
    }
}
