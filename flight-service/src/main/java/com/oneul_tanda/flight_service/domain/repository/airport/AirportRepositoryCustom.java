package com.oneul_tanda.flight_service.domain.repository.airport;

import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AirportRepositoryCustom {

    Page<AirportEntity> searchByKeyword(String keyword, Pageable pageable);
}
