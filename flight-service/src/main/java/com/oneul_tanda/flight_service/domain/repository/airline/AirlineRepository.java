package com.oneul_tanda.flight_service.domain.repository.airline;

import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AirlineRepository {

    AirlineEntity save(AirlineEntity airline);

    @Query("SELECT al FROM AirlineEntity al WHERE al.code = :code AND al.deletedAt IS NULL")
    Optional<AirlineEntity> findByCode(String code);

    @Query("SELECT al FROM AirlineEntity al WHERE al.id = :airlineId AND al.deletedAt IS NULL")
    Optional<AirlineEntity> findById(UUID airlineId);

    @Query("""
                SELECT al FROM AirlineEntity al
                WHERE (:code IS NULL OR al.code = :code)
                  AND (:name IS NULL OR al.name = :name)
                  AND al.deletedAt IS NULL
            """)
    Page<AirlineEntity> findByCodeAndName(@Param("code") String code, @Param("name") String name, Pageable adjusted);
}
