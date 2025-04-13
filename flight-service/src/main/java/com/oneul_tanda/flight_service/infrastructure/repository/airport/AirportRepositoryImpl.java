package com.oneul_tanda.flight_service.infrastructure.repository.airport;

import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.entity.QAirport;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AirportRepositoryImpl implements AirportRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Airport> searchByKeyword(String keyword, Pageable pageable) {
        QAirport airport = QAirport.airport;

        // BooleanBuilder를 사용하여 조건 생성
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.or(airport.name.containsIgnoreCase(keyword))
                .or(airport.city.containsIgnoreCase(keyword))
                .or(airport.code.containsIgnoreCase(keyword))
                .or(airport.country.containsIgnoreCase(keyword));

        // 결과 조회
        List<Airport> result = jpaQueryFactory
                .selectFrom(airport)
                .where(whereClause.and(airport.deletedAt.isNull()))
                .orderBy(airport.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        Long total = jpaQueryFactory
                .select(airport.count())
                .from(airport)
                .where(whereClause.and(airport.deletedAt.isNull()))
                .fetchOne();

        return new PageImpl<>(result, pageable, Optional.ofNullable(total).orElse(0L));
    }
}

