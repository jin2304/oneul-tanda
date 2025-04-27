package com.sparta.queueservice.application.service;

import com.sparta.queueservice.application.dto.FlightRequestDto;
import com.sparta.queueservice.application.dto.QueueResponseDto;
import com.sparta.queueservice.infrastructure.client.FlightResponse;
import com.sparta.queueservice.infrastructure.kafka.ProducerService;
import com.sparta.queueservice.infrastructure.kafka.event.EventStatusEnum;
import com.sparta.queueservice.infrastructure.client.FlightClient;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class QueueService {
    private final ZSetOperations<String, String> rankOps;
    private final FlightClient flightClient;
    private final ProducerService producerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    public QueueService(FlightClient airportClient,
                        RedisTemplate<String, String> redisTemplate,
                        ProducerService producerService,
                        RedissonClient redissonClient) {
        this.flightClient = airportClient;
        this.rankOps = redisTemplate.opsForZSet();
        this.producerService = producerService;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    // 예약 신청시 대기열 진입후 대기열 선점
    public QueueResponseDto tryReserve(FlightRequestDto request, UUID userId) {
        // 예약 신청한 flightId 와 좌석 수
        UUID flightId = request.getFlightId();
        Integer seatCount = request.getSeatCount();

        log.info("예약 요청: flightId={}, seatCount={}, userId={}",
                request.getFlightId(), request.getSeatCount(), userId);
        // 중복 예약 체크
        if(existReserve(flightId, userId)) {
            log.info("중복된 항공편 입니다. flightId: {} ", flightId);
            return QueueResponseDto.of(EventStatusEnum.DUPLICATE, "중복된 항공편 입니다.");
        }

        setExistReserve(flightId, userId);
        // 대기열에 들어온 순서대로 정렬 (시간순으로 정렬 - 같은 시간을 대비해 난수를 더하기)
        long score = System.nanoTime() + (long)(Math.random() * 1000);
        String reserveInfo = userId + ":" + seatCount;
        rankOps.add("ranks:" +  flightId, reserveInfo, score);

       return processReserve(flightId);
    }

    public QueueResponseDto processReserve(UUID flightId) {
        RLock lock = redissonClient.getLock("lock:flight:" + flightId);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);

            if (isLocked) {
                return processReserveWithLock(flightId);
            } else {
                log.warn("좌석 선점 락 획득 실패: {}", flightId);
                return QueueResponseDto.of(EventStatusEnum.FAILED, "좌석 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return QueueResponseDto.of(EventStatusEnum.FAILED, "스레드 인터셉트가 발생했습니다.");
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    //대기열 진입 후 선점 과정
    public QueueResponseDto processReserveWithLock(UUID flightId) {
        String key = "ranks:" +  flightId;
        // flightId를 받아 좌석 수를 조회
        FlightResponse flightResponse = flightClient.getFlight(flightId);
        Integer remainingSeats = flightResponse.getRemainingSeats();
//        테스트시 동시성 제어를 위한 redis 저장
//        String remainingSeatsStr = redisTemplate.opsForValue().get("seat:" + flightId);
////         값이 없다면, 기본 값인 10을 설정하여 redis 에 저장
//        int remainingSeats = (remainingSeatsStr != null) ? Integer.parseInt(remainingSeatsStr) : 4;
//        if (remainingSeatsStr == null) {
//            // 최초 저장 시에 redis 에 값 설정
//            redisTemplate.opsForValue().set("seat:" + flightId, String.valueOf(remainingSeats));
//            log.info("좌석 수 최초 설정: {}", remainingSeats);
//        }

        // 대기열에 있는 모든 유저 조회
        Set<String> topUsers = rankOps.range(key, 0, -1);
        if (topUsers == null || topUsers.isEmpty()) {
            return QueueResponseDto.of(EventStatusEnum.FAILED, "대기열에 없는 유저 입니다.");
        }
        // 좌석 수가 남아 있을때 대기열 선점 좌석 수가 0이면 실패 메시지를 보낸 후 대기열에서 삭제
        for(String reserveInfo : topUsers) {
            String[] parts =  reserveInfo.split(":");
            UUID userId = UUID.fromString(parts[0]);
            int seatCount = Integer.parseInt(parts[1]);

            if (!isTopUser(userId, flightId)) {
                log.info("순서가 아닙니다.");
                return QueueResponseDto.of(EventStatusEnum.FAILED, "순서가 아닙니다.");
            }

            if(seatCount <= remainingSeats) { // 대기열 선점 성공시 항공편의 좌석 수 차감 후 성공 메세지 전달
                // 좌석 수 차감 api 필요 (임시 좌석 차감 로직)
//                remainingSeats -= seatCount;
//                redisTemplate.opsForValue().set("seat:" + flightId, String.valueOf(remainingSeats));
                // 실제 항공편 서비스 좌석 차감
                flightClient.decreaseSeats(flightId, seatCount);
                log.info("대기열 선점에 성공 했습니다. 남은 좌석 수: {}", remainingSeats);
                rankOps.remove(key, reserveInfo);
                producerService.sendReserveSuccess(flightId, userId, seatCount, EventStatusEnum.SUCCESS);
                return QueueResponseDto.of(EventStatusEnum.SUCCESS, "대기열 선점에 성공했습니다.");
            } else {
                log.info("남은 좌석이 없습니다. 남은 좌석 수: {}", remainingSeats);
                rankOps.remove(key, reserveInfo);
                deleteExistReserve(flightId, userId);
                return QueueResponseDto.of(EventStatusEnum.FAILED, "남은 좌석이 없습니다.");
            }
        }
        return QueueResponseDto.of(EventStatusEnum.FAILED, "예약 신청에 실패하셨습니다.");
    }

    // 중복 유저가 있는지 체크
    private boolean existReserve(UUID flightId, UUID userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        return redisTemplate.hasKey(key);
    }

    // userId와 flightId를 redis 에 저장해 중복 체크
    private void setExistReserve(UUID flightId, UUID userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        log.info("중복 예약 방지 키: {}", key);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(5));
    }

    // 대기열 선점 실패시 sortedSet 과 같이 삭제
    public void deleteExistReserve(UUID flightId, UUID userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        redisTemplate.delete(key);
    }

    // 본인이 최상위 유저인지 아닌지 체크
    private boolean isTopUser(UUID userId, UUID flightId) {
        String topUser = rankOps.range("ranks:" + flightId, 0, 0).stream().findFirst().orElse(null);
        return topUser != null && topUser.startsWith(userId.toString());
    }
}
