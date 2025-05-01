package com.oneul_tanda.reservation_service.reservation.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.passenger.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.application.dto.HoldReservationData;
import com.oneul_tanda.reservation_service.reservation.application.dto.PassengerDto;
import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.client.PaymentClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.CreatePaymentInfo;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV2;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;


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
     * 예약 임시 생성 V1
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
     * 예약 임시 생성 V2
     */
    @Override
    public void createHoldReservationV2(CreateHoldReservationCommand command) {

        UUID flightId = command.flightId();
        UUID userId = command.userId();
        int seatCount = command.seatCount();

        String key = "HoldReservation:" + flightId + ":" + userId;

        // 중복 임시 예약 생성 검증
        validateDuplicateHoldReservation(key);

        // 임시 예약 데이터 생성
        HoldReservationData holdData = HoldReservationData.of(seatCount, new ArrayList<>());

        // 임시 예약 데이터 Redis에 저장
        try {
            String value = objectMapper.writeValueAsString(holdData);
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(2));
            log.info("임시 예약 생성 완료: {}", key);

        } catch (JsonProcessingException e) {
            log.error("임시 예약 저장 실패: {}", e.getMessage(), e);
            // Todo 좌석 복구 or 알림 시스템에 예약 임시 생성 실패 전송
            throw new CustomException(ReservationErrorCode.REDIS_SAVE_FAILED);
        }

        // Todo 예약 임시 생성 완료 이벤트 발행 or 알림 시스템에 예약 임시 생성 완료 전송
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
     * 예약 확정 V1 (1.탑승객 정보 입력 + 2.결제 -> 예약 확정)
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
     * 예약 확정 V2 (1.임시 예약 조회 및 검증 -> 2.탑승객 정보 입력 -> 3.예약 생성 -> 4.결제 -> 5.예약 확정)
     */
    @Override
    public ConfirmReservationResponseDto confirmReservationV2(ConfirmReservationCommandV2 command) {

        UUID flightId = command.flightId();
        UUID userId = command.userId();
        String key = "HoldReservation:" + flightId + ":" + userId;
        List<PassengerDto> passengers;


        // 1. Redis에서 임시 예약 조회 및 검증
        HoldReservationData holdData = validateAndGetHoldReservation(key);
        log.info("임시 예약 조회: {}", holdData);



        // 2. 탑승객 정보 입력
        if (command.passengers() != null && !command.passengers().isEmpty()) {
            // 탑승객 정보가 요청에 있으면 (사용자가 처음 요청할 때)
            passengers = command.passengers();
            holdData = HoldReservationData.of(holdData.seatCount(), passengers);

            try {
                String updatedValue = objectMapper.writeValueAsString(holdData);
                redisTemplate.opsForValue().set(key, updatedValue, Duration.ofMinutes(5));
            } catch (JsonProcessingException e) {
                log.error("Redis 탑승객 정보 업데이트 실패: {}", e.getMessage(), e);
                throw new CustomException(ReservationErrorCode.REDIS_SAVE_FAILED);
            }


        } else {
            // 탑승객 정보가 요청에 없으면 (결제 실패 후, 재시도할 때 -> Redis에 있는 탑승객 정보 사용)
            if (holdData.passengers() == null || holdData.passengers().isEmpty()) {
                throw new CustomException(ReservationErrorCode.PASSENGERS_NOT_FOUND);
            }
            passengers = holdData.passengers();
        }



        // 3. 예약 생성
        GetFlightInfo flightInfo = flightClient.getFlight(flightId);
        List<Ticket> ticketList = createTicketsFromHoldData(userId, flightInfo, passengers);
        Reservation reservation = Reservation.createReservation(userId, ticketList);
        reservationRepository.save(reservation);


        // 4. 결제 요청
        CreatePaymentInfo paymentInfo = requestPayment(reservation);
        if (paymentInfo == null || !"PAID".equalsIgnoreCase(paymentInfo.status())) {
            throw new CustomException(ReservationErrorCode.PAYMENT_FAILED);
        }


        // 5. 결제 성공 -> 예약 확정
        reservation.confirmReservation();
        reservationRepository.save(reservation);

        // Redis 키 삭제
        redisTemplate.delete(key);

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

    // userId, flightInfo, Redis passengers 기반 티켓 리스트 생성
    private List<Ticket> createTicketsFromHoldData(UUID userId, GetFlightInfo flightInfo, List<PassengerDto> passengers) {

        List<Ticket> tickets = new ArrayList<>();

        for (PassengerDto p : passengers) {
            Passenger passenger = Passenger.createPassenger(
                    userId,
                    p.name(),
                    p.birth(),
                    p.gender(),
                    p.passportNumber()
            );

            Ticket ticket = Ticket.createTicket(
                    passenger,
                    flightInfo.id(),
                    userId,
                    SeatClass.ECONOMY,
                    flightInfo.price(),
                    flightInfo.departureDate(),
                    flightInfo.arrivalDate()
            );

            tickets.add(ticket);
        }
        return tickets;
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






    // === 검증 메서드 === //
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


    // 중복 임시 예약 생성 검증
    private void validateDuplicateHoldReservation(String key) {
        if (redisTemplate.hasKey(key)) {
            throw CustomException.from(ReservationErrorCode.RESERVATION_DUPLICATE);
        }
    }


    // Redis에서 임시 예약 조회 및 검증
    private HoldReservationData validateAndGetHoldReservation(String key) {

        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new CustomException(ReservationErrorCode.HOLD_RESERVATION_EXPIRED);
        }

        try {
            return objectMapper.readValue(value, HoldReservationData.class);
        } catch (JsonProcessingException e) {
            log.error("Redis 데이터 역직렬화 실패: {}", e.getMessage(), e);
            throw new CustomException(ReservationErrorCode.REDIS_LOAD_FAILED);
        }
    }


}
