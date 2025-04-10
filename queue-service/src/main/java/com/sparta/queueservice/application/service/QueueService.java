package com.sparta.queueservice.application.service;

import com.sparta.queueservice.application.dto.FlightRequestDto;
import com.sparta.queueservice.infrastructure.Kafka.ProducerService;
import com.sparta.queueservice.infrastructure.client.FlightClient;
import com.sparta.queueservice.infrastructure.client.FlightResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
public class QueueService {
    private final ZSetOperations<String, String> rankOps;
//    private final FlightClient flightClient;
//    private final ProducerService producerService;
    private final RedisTemplate<String, String> redisTemplate;
    int remainingSeats = 10; // 테스트용 남은 좌석 수

    public QueueService(FlightClient airportClient,
                        RedisTemplate<String, String> redisTemplate,
                        ProducerService producerService) {
//        this.flightClient = airportClient;
        this.rankOps = redisTemplate.opsForZSet();
//        this.producerService = producerService;
        this.redisTemplate = redisTemplate;
    }

    // 예약 신청시 대기열 진입후 대기열 선점
    public void tryReserve(FlightRequestDto request, String userId) {
        // 예약 신청한 flightId 와 좌석 수
        String flightId = request.getFlightId();
        int seatCount = request.getSeatCount();
        log.info("예약 요청: flightId={}, seatCount={}, userId={}",
                request.getFlightId(), request.getSeatCount(), userId);
        // 중복 예약 체크
        if(existReserve(flightId, userId)) {
            log.info("중복된 항공편 입니다. flightId: {} ", flightId);
            return;
        }
        setExistReserve(flightId, userId);
        // 대기열에 들어온 순서대로 정렬 (시간순으로 정렬 - 같은 시간을 대비해 난수를 더하기)
        long score = System.currentTimeMillis() + (long)(Math.random()*1000);
        String reserveInfo = userId + ":" + seatCount;
        rankOps.add("ranks:" +  flightId, reserveInfo, score);

        processReserve(flightId);
    }

    //대기열 진입 후 선점 과정
    public void processReserve(String flightId) {
        String key = "ranks:" +  flightId;
        // flightId를 받아 좌석 수를 조회
//        FlightResponse flightResponse = flightClient.getAirport(flightId);
//        int remainingSeats = flightResponse.getRemainingSeats();

        // 대기열에 있는 모든 유저 조회
        Set<String> topUsers = rankOps.range(key, 0, -1);
        if (topUsers == null || topUsers.isEmpty()) {
            return;
        }
        // 좌석 수가 남아 있을때 대기열 선점 좌석 수가 0이면 실패 메시지를 보낸 후 대기열에서 삭제
        if  (remainingSeats <= 0) {
            for(String reserveInfo : topUsers) {
                String[] parts =  reserveInfo.split(":");
                String userId = parts[0];
                int seatCount = Integer.parseInt(parts[1]);

//                producerService.sendReserveFailed(flightId, userId, seatCount);
                rankOps.remove(key, reserveInfo);
            }
            return;
        }
        for(String reserveInfo : topUsers) {
            String[] parts =  reserveInfo.split(":");
            String userId = parts[0];
            int seatCount = Integer.parseInt(parts[1]);

            if(seatCount <= remainingSeats) { // 대기열 선점 성공시 항공편의 좌석 수 차감 후 성공 메세지 전달
                // 좌석 수 차감 api 필요
                remainingSeats -= seatCount;
                log.info("대기열 선점에 성공 했습니다. 남은 좌석 수: {}", remainingSeats);
//                producerService.sendReserveSuccess(flightId, userId, seatCount);
//                deleteExistReserve(flightId, userId);
//                rankOps.remove(key, reserveInfo);
            } else { // 대기열 선점 실패서 실패 메세지 전달
                log.info("대기열 선점에 실패했습니다. 남은 좌석 수: {}", remainingSeats);
//                producerService.sendReserveFailed(flightId, userId, seatCount);
//                deleteExistReserve(flightId, userId);
//                rankOps.remove(key, reserveInfo);
            }
            break;
        }
    }

    // 중복 유저가 있는지 체크
    private boolean existReserve(String flightId, String userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        return redisTemplate.hasKey(key);
    }

    // userId와 flightId를 redis 에 저장해 중복 체크
    private void setExistReserve(String flightId, String userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        log.info("key: {}", key);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(5));
    }

    // 대기열 선점 실패시 sortedSet 과 같이 삭제
    public void deleteExistReserve(String flightId, String userId) {
        String key = "reserve:" +  flightId + ":" + userId;
        redisTemplate.delete(key);
    }
}
