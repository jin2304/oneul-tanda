package com.oneul_tanda.flight_service.application.service.airport;

import com.oneul_tanda.flight_service.application.dtos.airport.CreateAirportCommand;
import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.application.dtos.airport.UpdateAirportCommand;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepositoryCustom;
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
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportRepositoryCustom airportRepositoryCustom;

    @Cacheable(value = "airports", key = "#airportId")
    public AirportResponse getAirport(UUID airportId) {

        AirportEntity airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

        return AirportResponse.from(airport);
    }

    public Page<AirportResponse> searchAirports(String keyword, Pageable pageable) {
        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<AirportEntity> airports = airportRepositoryCustom.searchByKeyword(keyword, adjusted);

        return airports.map(AirportResponse::from);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public AirportResponse createAirport(CreateAirportCommand airportCommand) {

        if (airportRepository.findByCode(airportCommand.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airport code " + airportCommand.getCode() + " already exists");
        }

        AirportEntity airport = AirportEntity.from(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

        airportRepository.save(airport);

        return AirportResponse.from(airport);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public AirportResponse updateAirport(UpdateAirportCommand airportCommand) {

        AirportEntity airport = airportRepository.findById(airportCommand.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

        airport.updateOf(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

//        airport.updateModificationInfo();

        return AirportResponse.from(airport);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public void deleteAirport(UUID airportId) {

        AirportEntity airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

//        airport.updateDeletionInfo();
    }
}
