package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import com.oneul_tanda.reservation_service.ticket.domain.entity.SeatClass;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

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
                            ticketDto.passenger().birth(),
                            ticketDto.passenger().gender(),
                            ticketDto.passenger().passportNumber()
            );

            // 티켓 생성
            Ticket ticket = Ticket.createTicket(
                    passenger,
                    ticketDto.flightId(),
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
     * 에약 임시 생성
     */
    @Override
    public CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command) {

        // TODO FeignClient 항공편 조회

        // 티켓 임시 생성
        List<Ticket> ticketList = new ArrayList<>();

        for (int i = 0; i < command.seatCount(); i++) {
            Ticket ticket = Ticket.createTicketWithoutPassenger(
                    command.flightId(),
                    // TODO 임시 값 설정, 항공편 조회에서 데이터 획득 고려
                    SeatClass.ECONOMY,
                    BigDecimal.valueOf(10000)
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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));
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
        // 1. 예약 조회 (예약 + 티켓)
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));


        // 2. 티켓 확정 (탑승객 정보 매핑)
        for (ConfirmReservationCommand.ConfirmTicketCommand ticketCommand : command.tickets()) {
            // 2-1. 해당 ticketId를 가진 티켓 찾기
            Ticket ticket = reservation.getTicketList().stream()
                    .filter(t -> t.getId().equals(ticketCommand.ticketId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 티켓을 찾을 수 없습니다."));

            // 2-2. 탑승객 생성 및 티켓에 확정 처리
            Passenger passenger = Passenger.createPassenger(
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
}
