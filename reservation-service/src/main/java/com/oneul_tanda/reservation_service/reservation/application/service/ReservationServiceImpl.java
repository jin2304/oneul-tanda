package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.client.PaymentClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.CreatePaymentInfo;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.ticket.domain.entity.SeatClass;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final FlightClient flightClient;
    private final PaymentClient paymentClient;


    /**
     * 예약 생성 (동기 처리)
     */
    @Override
    public CreateReservationResponseDto createReservation(CreateReservationCommand command) {

        // 중복 예약 생성 검증
        validateDuplicateReservation(command.userId(), command.flightId());

        // FeignClient 항공편 조회 및 데이터 획득
        GetFlightInfo flightInfo = flightClient.getFlight(command.flightId());

        // 좌석 수 검증 및 좌석 차감
        validateSeatAndReserve(command, flightInfo);

        // 티켓 생성 (탑승객 정보o)
        List<Ticket> ticketList = createTicketsWithPassengers(command, flightInfo);

        // 예약 생성
        Reservation reservation = Reservation.createReservation(command.userId(), ticketList);

        return CreateReservationResponseDto.from(reservationRepository.save(reservation));
    }



    /**
     * 예약 임시 생성
     */
    @Override
    public CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command) {

        // 중복 예약 생성 검증
        validateDuplicateReservation(command.userId(), command.flightId());

        // FeignClient 항공편 조회 및 데이터 획득
        GetFlightInfo flightInfo = flightClient.getFlight(command.flightId());

        // 티켓 임시 생성 (탑승객 정보x)
        List<Ticket> ticketList = createTicketsWithoutPassengers(command, flightInfo);

        // 예약 임시 생성
        Reservation reservation = Reservation.createHoldReservation(command.userId(), ticketList);

        return CreateHoldReservationResponseDto.from(reservationRepository.save(reservation));
    }




    /**
     * 예약 단일 조회
     */
    @Override
    @Transactional(readOnly = true)
    public ReadReservationResponseDto readReservation(UUID reservationId) {
        // 1. 예약 조회
        Reservation reservation = getReservationOrThrow(reservationId);
        return ReadReservationResponseDto.from(reservation);
    }



    /**
     * 예약 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReadReservationResponseDto> readAllReservation(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReadReservationResponseDto::from);
    }




    /**
     * 예약 확정 (1.탑승객 정보 입력 + 2.결제 -> 예약 확정)
     */
    @Override
    public ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand command) {
        // 예약 조회
        Reservation reservation = getReservationOrThrow(command.reservationId());

        // 1. 탑승객 정보 입력 (티켓에 탑승객 정보 매핑)
        if (reservation.isPassengerInfoInputPossible()) {
            registerPassengerInfo(command, reservation);
            reservation.completePassengerInfo();
            reservationRepository.save(reservation);
        }

        // 2. 결제 요청
        CreatePaymentInfo paymentInfo = requestPayment(reservation);

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
     * 예약 취소 (예약 수정)
     */
    @Override
    @Transactional
    public CancelReservationResponseDto cancelReservation(UUID reservationId) {
        // 1. 예약 조회
        Reservation reservation = getReservationOrThrow(reservationId);

        // 2. 예약 취소
        reservation.cancel();

        // 3. 선점된 좌석 복구
        restoreReservedSeats(reservation);

        // 4. 응답 반환
        return CancelReservationResponseDto.of(reservation.getId());
    }



    
    // 예약 조회
    private Reservation getReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    // 티켓 생성
    private List<Ticket> createTicketsWithPassengers(CreateReservationCommand command, GetFlightInfo flightInfo) {

        List<Ticket> ticketList = new ArrayList<>();

        for (CreateReservationCommand.CreatePassengerCommand passengerCommand : command.passengers()) {
            Passenger passenger = Passenger.createPassenger(
                    command.userId(),
                    passengerCommand.name(),
                    passengerCommand.birth(),
                    passengerCommand.gender(),
                    passengerCommand.passportNumber()
            );

            Ticket ticket = Ticket.createTicket(
                    passenger,
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
    private void registerPassengerInfo(ConfirmReservationCommand command, Reservation reservation) {

        for (ConfirmReservationCommand.ConfirmTicketCommand ticketCommand : command.tickets()) {
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
    public CreatePaymentInfo requestPayment(Reservation reservation) {

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


    // 예약 내에서 특정 티켓 조회 및 존재 여부 검증
    private Ticket validateAndGetTicket(Reservation reservation, ConfirmReservationCommand.ConfirmTicketCommand ticketCommand) {
        return reservation.getTicketList().stream()
                .filter(t -> t.getId().equals(ticketCommand.ticketId()))
                .findFirst()
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.TICKET_NOT_FOUND));
    }


    // 중복 예약 생성 검증
    private void validateDuplicateReservation(UUID userId, UUID flightId) {
        if (reservationRepository.findByUserIdAndFlightId(userId, flightId).isPresent()) {
            throw CustomException.from(ReservationErrorCode.RESERVATION_DUPLICATE);
        }
    }


    // 좌석 수 검증 및 좌석 차감
    private void validateSeatAndReserve(CreateReservationCommand command, GetFlightInfo flightInfo) {
        if (flightInfo.remainingSeats() < command.seatCount()) {
            throw CustomException.from(ReservationErrorCode.FLIGHT_SEAT_NOT_ENOUGH);
        }
        flightClient.decreaseSeats(command.flightId(), command.seatCount());
    }


}
