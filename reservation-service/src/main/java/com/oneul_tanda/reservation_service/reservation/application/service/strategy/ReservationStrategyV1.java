package com.oneul_tanda.reservation_service.reservation.application.service.strategy;

import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.client.PaymentClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.PaymentInfo;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV1;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Ticket;
import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.SeatClass;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;

import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDtoV1;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("reservationStrategyV1")
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationStrategyV1 implements ReservationStrategy {


    private final ReservationRepository reservationRepository;
    private final FlightClient flightClient;
    private final PaymentClient paymentClient;


    /**
     * 예약 임시 생성 V1
     */
    @Override
    public CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command) {

        // 중복 예약 생성 검증
        if(validateDuplicateReservation(command.userId(), command.flightId())){
            log.warn("[V1 임시 예약 저장 실패: 중복 예약] userId={}, flightId={}", command.userId(), command.flightId());
            return CreateHoldReservationResponseDtoV1.Failed();
        }

        // FeignClient 항공편 조회 및 데이터 획득
        GetFlightInfo flightInfo = flightClient.getFlight(command.flightId());

        // 티켓 임시 생성 (탑승객 정보x)
        List<Ticket> ticketList = createTicketsWithoutPassengers(command, flightInfo);

        // 예약 임시 생성
        Reservation reservation = Reservation.createHoldReservation(command.userId(), ticketList);

        return CreateHoldReservationResponseDtoV1.Success(reservationRepository.save(reservation));
    }



    /**
     * 예약 확정 V1 (1.탑승객 정보 입력 + 2.결제 -> 예약 확정)
     */
    @Override
    public ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand commandV1) {

        if (!(commandV1 instanceof ConfirmReservationCommandV1 command)) {
            throw new CustomException(ReservationErrorCode.INVALID_COMMAND_TYPE);
        }

        // 예약 조회
        Reservation reservation = getReservationOrThrow(command.reservationId());

        // 1. 탑승객 정보 입력 (티켓에 탑승객 정보 매핑)
        if (reservation.isPassengerInfoInputPossible()) {
            registerPassengerInfo(command, reservation);
            reservation.completePassengerInfo();
            reservationRepository.save(reservation);
        }

        // 2. 결제 요청
        PaymentInfo paymentInfo = requestPayment(reservation);

        // 결제 실패 처리
        if (paymentInfo == null || !paymentInfo.status().equalsIgnoreCase("PAID")) {
            reservation.completePaymentFailure();
            reservationRepository.save(reservation);
            throw new CustomException(ReservationErrorCode.PAYMENT_FAILED);
        }

        // 결제 성공 -> 예약 확정 처리
        reservation.confirmReservation();
        reservationRepository.save(reservation);

        return ConfirmReservationResponseDto.from(reservation);
    }



    /**
     * 예약 취소
     */
    @Override
    @Transactional
    public CancelReservationResponseDto cancelReservation(UUID reservationId) {
        // 예약 조회
        Reservation reservation = getReservationOrThrow(reservationId);

        try {
            // 1. 좌석 복구
            restoreReservedSeats(reservation);

            // 2. 결제 취소(환불) 요청
            cancelPayment(reservation);

            // 3. 예약 취소
            reservation.cancel();

            return CancelReservationResponseDto.of(reservation.getId());

        } catch (CustomException e) {

            // 좌석 복구 자체 실패 -> 보상 필요 없음
            if (e.getErrorCode() == ReservationErrorCode.FLIGHT_SEAT_RESTORE_FAILED) {
                log.warn("좌석 복구 실패 - 보상 생략");
                throw e;
            }

            // 좌석 복구는 성공, 결제 취소 실패 -> 보상 트랜잭션 수행
            try {
                log.warn("결제 취소 실패 - 보상 트랜잭션 수행");
                compensateReservedSeats(reservation); // 좌석 복구 롤백 -> 다시 차감
            } catch (Exception rollbackEx) {
                log.error("좌석 복구 보상(롤백) 실패 - 수동 조치 필요: flightId={}, error={}",
                        reservation.getTicketList().get(0).getFlightId(), rollbackEx.getMessage(), rollbackEx);
                // TODO: Slack 알림, DLQ 등
            }
            throw e;
        }

    }



    // 예약 조회
    private Reservation getReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }


    // 티켓 임시 생성
    private List<Ticket> createTicketsWithoutPassengers(CreateHoldReservationCommand command, GetFlightInfo flightInfo) {

        List<Ticket> ticketList = new ArrayList<>();

        for (int i = 0; i < command.seatCount(); i++) {
            Ticket ticket = Ticket.createTicketWithoutPassenger(
                    command.flightId(),
                    command.userId(),
                    SeatClass.ECONOMY,
                    flightInfo.price(),
                    flightInfo.departureDate(),
                    flightInfo.arrivalDate()
            );
            ticketList.add(ticket);
        }

        return ticketList;
    }


    // 탑승객 정보 입력(티켓 확정)
    private void registerPassengerInfo(ConfirmReservationCommandV1 command, Reservation reservation) {

        for (ConfirmReservationCommandV1.ConfirmTicketCommand ticketCommand : command.tickets()) {
            // 예약 내에서 특정 티켓 조회 및 존재 여부 검증
            Ticket ticket = validateAndGetTicket(reservation, ticketCommand);

            // 탑승객 생성
            Passenger passenger = Passenger.createPassenger(
                    command.userId(),
                    ticketCommand.passenger().name(),
                    ticketCommand.passenger().birth(),
                    ticketCommand.passenger().gender(),
                    ticketCommand.passenger().passportNumber()
            );

            // 티켓에 탑승객 정보 매핑
            ticket.confirmTicket(passenger);
        }
    }


    // 결제 요청
    public PaymentInfo requestPayment(Reservation reservation) {

        // 결제 가능 상태 검증
        if (!reservation.isPayable()) {
            throw new CustomException(ReservationErrorCode.PAYMENT_NOT_ALLOWED);
        }

        // 결제 요청
        try {
            return paymentClient.confirmPayment(
                    reservation.getId(),
                    reservation.getTotalPrice()
            );

        } catch (Exception e) {
            log.error("결제 예외 발생 - ReservationId: {}", reservation.getId(), e);
            return null;
        }
    }


    // 선점된 좌석 복구
    private void restoreReservedSeats(Reservation reservation) {

        Integer seatCount = reservation.getTicketList().size();
        UUID flightId = reservation.getTicketList().get(0).getFlightId();

        // 좌석 복구 요청
        // TODO: 분산 트랜잭션 어떻게 관리? 1. Saga 패턴의 보상트랜잭션,  2. 이벤트 발행, 3. 기타
        try {
            flightClient.increaseSeats(flightId, seatCount);

        } catch (Exception e) {
            log.error("좌석 복원 실패 - flightId={}, seatCounts={}, error={}", flightId, seatCount, e.getMessage(), e);
            throw CustomException.from(ReservationErrorCode.FLIGHT_SEAT_RESTORE_FAILED);
        }
    }


    // 결제 취소(환불) 요청
    private void cancelPayment(Reservation reservation) {
        try {
            paymentClient.cancelPayment(reservation.getId());

        } catch (Exception e) {
            log.error("결제 취소 실패 - reservationId={}, error={}", reservation.getId(), e.getMessage(), e);
            throw CustomException.from(ReservationErrorCode.PAYMENT_REFUND_FAILED);
        }
    }


    // 보상 트랜잭션: 좌석 차감 보상(복구 취소)
    private void compensateReservedSeats(Reservation reservation) {
        Integer seatCount = reservation.getTicketList().size();
        UUID flightId = reservation.getTicketList().get(0).getFlightId();

        try {
            flightClient.decreaseSeats(flightId, seatCount);
        } catch (Exception e) {
            log.error("좌석 차감 보상 실패 - flightId={}, seatCount={}, error={}",
                    flightId, seatCount, e.getMessage(), e);
            throw CustomException.from(ReservationErrorCode.FLIGHT_SEAT_COMPENSATION_FAILED);
        }
    }




    // === 검증 메서드 === //
    // 예약 내에서 특정 티켓 조회 및 존재 여부 검증
    private Ticket validateAndGetTicket(Reservation reservation, ConfirmReservationCommandV1.ConfirmTicketCommand ticketCommand) {
        return reservation.getTicketList().stream()
                .filter(t -> t.getId().equals(ticketCommand.ticketId()))
                .findFirst()
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.TICKET_NOT_FOUND));
    }


    // 중복 예약 생성 검증
    private boolean validateDuplicateReservation(UUID userId, UUID flightId) {
        return reservationRepository.findByUserIdAndFlightId(userId, flightId).isPresent();
    }
}
