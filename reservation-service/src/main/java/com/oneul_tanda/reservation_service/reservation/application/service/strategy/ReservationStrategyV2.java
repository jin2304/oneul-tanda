package com.oneul_tanda.reservation_service.reservation.application.service.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneul_tanda.reservation_service.common.exception.CustomException;
import com.oneul_tanda.reservation_service.reservation.application.client.FlightClient;
import com.oneul_tanda.reservation_service.reservation.application.client.PaymentClient;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.GetFlightInfo;
import com.oneul_tanda.reservation_service.reservation.application.client.dto.response.PaymentInfo;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.command.ConfirmReservationCommandV2;
import com.oneul_tanda.reservation_service.reservation.application.command.CreateHoldReservationCommand;
import com.oneul_tanda.reservation_service.reservation.application.dto.HoldReservationData;
import com.oneul_tanda.reservation_service.reservation.application.dto.PassengerDto;
import com.oneul_tanda.reservation_service.reservation.application.exception.ReservationErrorCode;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Passenger;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Reservation;
import com.oneul_tanda.reservation_service.reservation.domain.entity.Ticket;
import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.SeatClass;
import com.oneul_tanda.reservation_service.reservation.domain.repository.ReservationRepository;
import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.KafkaReservationProducer;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create.CreateHoldReservationResponseDtoV2;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.CancelReservationResponseDto;
import com.oneul_tanda.reservation_service.reservation.presentation.dto.response.update.ConfirmReservationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Primary
@Service("reservationStrategyV2")
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationStrategyV2 implements ReservationStrategy {

    private final ReservationRepository reservationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final FlightClient flightClient;
    private final PaymentClient paymentClient;
    private final KafkaReservationProducer producer;
    private final ObjectMapper objectMapper;


    /**
     * 예약 임시 생성 V2
     */
    @Override
    public CreateHoldReservationResponseDto createHoldReservation(CreateHoldReservationCommand command) {

        UUID flightId = command.flightId();
        UUID userId = command.userId();
        int seatCount = command.seatCount();

        String key = "HoldReservation:" + flightId + ":" + userId;

        // 중복 임시 예약 생성 검증
        if(validateDuplicateHoldReservation(key)){
            log.warn("[V2 임시 예약 저장 실패: 중복 예약] userId={}, flightId={}", command.userId(), command.flightId());
            return CreateHoldReservationResponseDtoV2.Failed();
        }

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
            return CreateHoldReservationResponseDtoV2.Failed();
        }

        // Todo 예약 임시 생성 완료 이벤트 발행 or 알림 시스템에 예약 임시 생성 완료 전송
        return CreateHoldReservationResponseDtoV2.Success();
    }



    /**
     * 예약 확정 V2 (1.임시 예약 조회 및 검증 -> 2.탑승객 정보 입력 -> 3.예약 생성 -> 4.결제 -> 5.예약 확정)
     */
    @Override
    public ConfirmReservationResponseDto confirmReservation(ConfirmReservationCommand commandV2) {

        if (!(commandV2 instanceof ConfirmReservationCommandV2 command)) {
            throw new CustomException(ReservationErrorCode.INVALID_COMMAND_TYPE);
        }

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
        PaymentInfo paymentInfo = requestPayment(reservation);
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
     * 예약 취소 V2
     */
    @Override
    @Transactional
    public CancelReservationResponseDto cancelReservation(UUID reservationId) {
        // 1. 예약 조회
        Reservation reservation = getReservationOrThrow(reservationId);

        // 2. 예약 취소
        reservation.requestCancellation();

        // 3. 이벤트 발행
        UUID flightId = reservation.getTicketList().get(0).getFlightId();
        int seatCount = reservation.getTicketList().size();
        producer.sendReservationCanceledEvent(reservation.getId(), flightId, reservation.getUserId(), seatCount);

        // 4. 응답 반환
        return CancelReservationResponseDto.of(reservation.getId());
    }




    // 예약 조회
    private Reservation getReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> CustomException.from(ReservationErrorCode.RESERVATION_NOT_FOUND));
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





    // === 검증 메서드 === //
    // 예약 내에서 특정 티켓 조회 및 존재 여부 검증
    // 중복 임시 예약 생성 검증
    private boolean  validateDuplicateHoldReservation(String key) {
        return redisTemplate.hasKey(key);
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
