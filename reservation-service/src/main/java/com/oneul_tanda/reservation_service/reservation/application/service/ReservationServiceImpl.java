package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.infrastructure.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.create.CreateReservationRequestDto;
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

    /**
     * 예약 생성
     */
    @Override
    public CreateReservationResponseDto createReservation(CreateReservationRequestDto requestDto) {

        // 탑승객 + 티켓 생성
        List<Ticket> ticketList = new ArrayList<>();

        for (CreateReservationRequestDto.CreateTicketRequestDto ticketDto : requestDto.tickets()) {
            // 탑승객 생성
            Passenger passenger = Passenger.createPassenger(
                            requestDto.userId(),
                            ticketDto.passenger().name(),
                            ticketDto.passenger().birth(),
                            ticketDto.passenger().gender(),
                            ticketDto.passenger().passportNumber()
            );

            // 티켓 생성
            Ticket ticket = Ticket.createTicket(
                    passenger,
                    ticketDto.flightId(),
                    requestDto.userId(),
                    ticketDto.seatClass(),
                    ticketDto.price()
            );

            ticketList.add(ticket);
        }


        // 예약 생성
        Reservation reservation = Reservation.createReservation(
                requestDto.userId(),
                ticketList
        );

        return CreateReservationResponseDto.from(reservationRepository.save(reservation));
    }



    /**
     * 예약 임시 생성
     */
    @Override
    public CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command) {

        // 중복 예약 생성 검증
        if (reservationRepository.findByUserIdAndFlightId(command.userId(), command.flightId()).isPresent()) {
            throw new IllegalStateException("이미 해당 항공편에 대한 임시 예약이 존재합니다.");
        }


        // FeignClient 항공편 조회 및 데이터 획득
        GetFlightInfo getFlightInfo = flightClient.getFlight(command.flightId());


        // 티켓 임시 생성
        List<Ticket> ticketList = new ArrayList<>();

        for (int i = 0; i < command.seatCount(); i++) {
            Ticket ticket = Ticket.createTicketWithoutPassenger(
                    command.flightId(),
                    command.userId(),
                    SeatClass.ECONOMY,
                    getFlightInfo.price(),
                    getFlightInfo.departureDate(),
                    getFlightInfo.arrivalDate()
            );

            ticketList.add(ticket);
        }


        // 예약 임시 생성
        Reservation reservation = Reservation.createHoldReservation(
                command.userId(),
                ticketList
        );

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
     * 예약 확정 (예약 수정)
     */
    @Override
    public ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand command) {
        // 1. 예약 조회
        Reservation reservation = getReservationOrThrow(command.reservationId());


        // 2. 티켓 확정 (탑승객 정보 매핑)
        for (ConfirmReservationCommand.ConfirmTicketCommand ticketCommand : command.tickets()) {
            // 2-1. 해당 ticketId를 가진 티켓 찾기
            Ticket ticket = reservation.getTicketList().stream()
                    .filter(t -> t.getId().equals(ticketCommand.ticketId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 티켓을 찾을 수 없습니다."));

            // 2-2. 탑승객 생성 및 티켓에 확정 처리
            Passenger passenger = Passenger.createPassenger(
                    command.userId(),
                    ticketCommand.passenger().name(),
                    ticketCommand.passenger().birth(),
                    ticketCommand.passenger().gender(),
                    ticketCommand.passenger().passportNumber()
            );

            ticket.confirmTicket(passenger);
        }

        // 3. 예약 상태를 확정으로 변경
        reservation.confirmReservation();

        // 4. 변경사항 저장 (티켓 및 탑승객 포함)
        reservationRepository.save(reservation);

        // 5. 응답 반환
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


        // 3. 좌석 수 추출
        Integer seatCount = reservation.getTicketList().size();


        // 4. 항공편 ID 추출 (모든 티켓이 동일한 flightId라고 가정)
        UUID flightId = reservation.getTicketList().get(0).getFlightId();


        // 5. 좌석 복구 요청
        // TODO: 분산 트랜잭션 어떻게 관리? 1. Saga 패턴의 보상트랜잭션,  2. 이벤트 발행, 3. 기타
        try {
            flightClient.increaseSeats(flightId, seatCount);

        } catch (Exception e) {
            log.error("좌석 복원 실패 - flightId={}, seatCounts={}, error={}", flightId, seatCount, e.getMessage(), e);
            throw new RuntimeException("좌석 복원 실패로 예약 취소 롤백");
        }

        // 6. 응답 반환 
        return CancelReservationResponseDto.of(reservation.getId());
    }



    
    // 예약 조회
    private Reservation getReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.NOT_FOUND));
    }

}
