package com.oneul_tanda.reservation_service.reservation.domain.entity;


import com.oneul_tanda.reservation_service.common.entity.BaseTimeEntity;
import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                .totalPrice(BigDecimal.ZERO)
                .status(ReservationStatus.PENDING)
                .build();

        // 예약과(Reservation)과 티켓(Ticket) 간의 양방향 연관 관계 설정
        for (Ticket ticket : ticketList) {
            reservation.addTicket(ticket);
        }

        // 총 가격 계산
        reservation.calculateTotalPrice();

        reservation.registerCreatedBy(userId);

        return reservation;
    }


    // 연관 관계 설정 메서드
    public void addTicket(Ticket ticket) {
        this.ticketList.add(ticket);   // 예약 티켓 리스트에 티켓 추가
        ticket.associateTicket(this);  // 티켓에서도 해당 예약 정보 설정, setter 대신 associateTicket 메서드 사용
    }


    // 총 가격 계산
    public void calculateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Ticket ticket : ticketList) {
            total = total.add(ticket.getUnitPrice());
        }
        this.totalPrice = total;
    }





    // === 검증 메서드 === //
    // 취소 가능 검증
    private void validateCancelable() {
        LocalDateTime now = LocalDateTime.now();

        if (this.status == ReservationStatus.CANCELED) {
            throw CustomException.from(ReservationErrorCode.CANNOT_CANCEL_ALREADY_CANCELED);
        }

        // 예약 생성 후 24시간 이내 여부 판단
        if (!isCreatedWithin24Hours(now)) {
            throw CustomException.from(ReservationErrorCode.CANNOT_CANCEL_AFTER_24H_CREATION);
        }

        // 항공편 출발까지 72시간 이상 남았는지 여부 판단
        if (!isDepartureAfter72Hours(now)) {
            throw CustomException.from(ReservationErrorCode.CANNOT_CANCEL_WITHIN_72H_TO_DEPARTURE);
         }
    }


    // 예약 생성 후 24시간 이내 여부 확인
    public boolean isCreatedWithin24Hours(LocalDateTime now) {
        // 예약 임시 생성 시간 (createdAt) 기준으로 24시간 이내에만 취소 가능
        return this.getCreatedAt().isAfter(now.minusHours(24));
        // 테스트 용
        //return this.getCreatedAt().isAfter(now.minusMinutes(1));
    }

    // 항공편 출발 72시간 이상 남았는지 여부 확인
    private boolean isDepartureAfter72Hours(LocalDateTime now) {
        // 항공편 출발 시간 (departureDate) 기준으로 72시간 이후에만 취소 가능
        return ticketList.stream()
                .map(Ticket::getDepartureDate)
                .allMatch(departureDate -> departureDate.isAfter(now.plusHours(72)));
    }

    // 결제 가능 여부 확인
    public boolean isPayable() {
        return this.status == ReservationStatus.PENDING ||
               this.status == ReservationStatus.PASSENGER_INFO_ENTERED ||
               this.status == ReservationStatus.PAYMENT_FAILED;
    }

    // 탑승객 정보 입력 가능 여부 확인
    public boolean isPassengerInfoInputPossible() {
        return this.status == ReservationStatus.PENDING;
    }




    // === 상태 변경 메서드 === //
    //탑승객 정보 입력 완료 상태로 변경
    public void completePassengerInfo() {
        this.status = ReservationStatus.PASSENGER_INFO_ENTERED;
    }


    // 예약 확정 상태로 변경
    public void confirmReservation() {
        this.status = ReservationStatus.RESERVED;
    }


    // 예약 취소 상태로 변경
    public void cancel() {
        validateCancelable();
        this.status = ReservationStatus.CANCELED;
    }


    // 결제 실패 상태로 변경
    public void completePaymentFailure() {
        this.status = ReservationStatus.PAYMENT_FAILED;
    }
}
