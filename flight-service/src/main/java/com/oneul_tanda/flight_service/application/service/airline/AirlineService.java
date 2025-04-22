package com.oneul_tanda.flight_service.application.service.airline;

import com.oneul_tanda.flight_service.application.dtos.airline.CreateAirlineCommand;
import com.oneul_tanda.flight_service.application.dtos.airline.UpdateAirlineCommand;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.util.PagingUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Cacheable(value = "airlines", key = "#airlineId")
    public AirlineResponse getAirline(UUID airlineId) {
        AirlineEntity airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

        return AirlineResponse.from(airline);
    }

    public Page<AirlineResponse> searchAirlines(String code, String name, Pageable pageable) {
        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<AirlineEntity> airlines = airlineRepository.findByCodeAndName(code, name, adjusted);

        return airlines.map(AirlineResponse::from);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public AirlineResponse createAirline(CreateAirlineCommand airlineCommand) {

        if (airlineRepository.findByCode(airlineCommand.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airline code " + airlineCommand.getCode() + " already exists");
        }
        AirlineEntity airline = AirlineEntity.from(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );
        airlineRepository.save(airline);

        return AirlineResponse.from(airline);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public AirlineResponse updateAirline(UpdateAirlineCommand airlineCommand) {

        AirlineEntity airline = airlineRepository.findById(airlineCommand.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

        airline.updateOf(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );

//        airline.updateModificationInfo();

        return AirlineResponse.from(airline);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public void deleteAirline(UUID airlineId) {

        AirlineEntity airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found"));

//        airline.updateDeletionInfo();
    }
}
