package com.oneul_tanda.reservation_service.reservation.application.service;

import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.request.CreateReservationRequestDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.read.ReadReservationResponseDto;
import com.oneul_tanda.reservation_service.ticket.domain.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 에약 생성
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
}
