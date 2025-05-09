package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.service.strategy.ReservationStrategy;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.DeleteReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.SeatClass;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Ticket;
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

    private final ReservationStrategy reservationStrategy;
    private final ReservationRepository reservationRepository;
    private final FlightClient flightClient;


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
        return reservationStrategy.createHoldReservation(command);
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
     * 예약 확정
     */
    @Override
    public ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand command) {
        return reservationStrategy.confirmReservation(command);
    }



    /**
     * 예약 취소
     */
    @Override
    @Transactional
    public CancelReservationResponseDto cancelReservation(UUID reservationId) {
        return reservationStrategy.cancelReservation(reservationId);
    }



    /**
     * 예약 취소 확정
     */
    @Override
    public void cancelReservationConfirm(UUID reservationId) {
         // 예약 조회
         Reservation reservation = getReservationOrThrow(reservationId);

         reservation.cancel();
     }



    /**
     * 예약 삭제
     */
    @Override
    public DeleteReservationResponseDto deleteReservation(UUID userId, UUID reservationId) {
        // 예약 조회
        Reservation reservation = getReservationOrThrow(reservationId);

        // 예약 삭제
        reservation.markDeleted(userId);

        return DeleteReservationResponseDto.of(reservation.getId());
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




    // === 검증 메서드 === //
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
