package com.oneul_tanda.flight_service.infrastructure.repository.flight;

import com.oneul_tanda.flight_service.domain.entity.FlightEntity;
import com.oneul_tanda.flight_service.domain.entity.QFlightEntity;
import com.oneul_tanda.flight_service.domain.repository.flight.FlightRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FlightRepositoryImpl implements FlightRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<FlightEntity> searchFlights(String departureAirport, String arrivalAirport,
                                            LocalDateTime departureDate, Integer requiredSeats, Pageable pageable) {
        QFlightEntity flight = QFlightEntity.flightEntity;
        BooleanBuilder whereClause = new BooleanBuilder();

        // 출발 공항 코드 (정확한 매칭)
        if (departureAirport != null && !departureAirport.isBlank()) {
            whereClause.and(flight.departureAirport.code.eq(departureAirport));
        }

        // 도착 공항 코드 (정확한 매칭)
        if (arrivalAirport != null && !arrivalAirport.isBlank()) {
            whereClause.and(flight.arrivalAirport.code.eq(arrivalAirport));
        }

        // 출발 날짜 (하루 범위로 검색)
        if (departureDate != null) {
//        LocalDateTime startOfDay = departureDate.truncatedTo(ChronoUnit.DAYS);
//        LocalDateTime endOfDay = startOfDay.plusDays(1);
            LocalDateTime maxDate = departureDate.plusMonths(1); // 1개월 이내의 항공편만 검색
//        whereClause.and(flight.departureDate.between(startOfDay, endOfDay));
            whereClause.and(flight.departureDate.between(departureDate, maxDate));
        }

        // 요구 좌석 수 (유효한 값만)
        if (requiredSeats != null && requiredSeats > 0) {
            whereClause.and(flight.remainingSeats.goe(requiredSeats));
        }

        // 삭제되지 않은 항공편만
        whereClause.and(flight.deletedAt.isNull());

        // 디버깅 로그
        log.debug("Params - departureAirport: {}, arrivalAirport: {}, departureDate: {}, requiredSeats: {}",
                departureAirport, arrivalAirport, departureDate, requiredSeats);
        log.debug("Query: {}", jpaQueryFactory.selectFrom(flight)
                .where(whereClause)
                .orderBy(flight.departureDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toString());

        // 결과 조회
        List<FlightEntity> result = jpaQueryFactory
                .selectFrom(flight)
                .where(whereClause)
                .orderBy(flight.departureDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        Long total = jpaQueryFactory
                .select(flight.count())
                .from(flight)
                .where(whereClause)
                .fetchOne();

        // 디버깅 로그: 결과 확인
        log.debug("Result - count: {}, flights: {}", result.size(), result);

        return new PageImpl<>(result, pageable, total != null ? total : 0L);
    }
}
