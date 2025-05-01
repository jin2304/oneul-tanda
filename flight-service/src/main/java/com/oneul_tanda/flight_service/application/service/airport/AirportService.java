package com.oneul_tanda.flight_service.application.service.airport;

import com.oneul_tanda.flight_service.application.dtos.airport.CreateAirportCommand;
import com.oneul_tanda.flight_service.application.dtos.airport.UpdateAirportCommand;
import com.oneul_tanda.flight_service.domain.entity.AirportEntity;
import com.oneul_tanda.flight_service.domain.exception.airport.AirportDuplicatedException;
import com.oneul_tanda.flight_service.domain.exception.airport.AirportNotFoundException;
import com.oneul_tanda.flight_service.domain.exception.common.GlobalException;
import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepositoryCustom;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.util.PagingUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportRepositoryCustom airportRepositoryCustom;

    @Cacheable(value = "airports", key = "#airportId")
    public AirportResponse getAirport(UUID airportId, String userRole) {
        validateUserRole(userRole);

        AirportEntity airport = getAirportById(airportId);

        return AirportResponse.from(airport);
    }

    public Page<AirportResponse> searchAirports(String keyword, Pageable pageable, String userRole) {
        validateUserRole(userRole);

        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<AirportEntity> airports = airportRepositoryCustom.searchByKeyword(keyword, adjusted);

        return airports.map(AirportResponse::from);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public AirportResponse createAirport(CreateAirportCommand airportCommand, UUID userId, String userRole) {
        validateUserRole(userRole);

        getAirportByCode(airportCommand);

        AirportEntity airport = AirportEntity.from(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

        airportRepository.save(airport);
        airport.updateCreationInfo(userId);

        return AirportResponse.from(airport);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public AirportResponse updateAirport(UpdateAirportCommand airportCommand, UUID userId, String userRole) {
        validateUserRole(userRole);

        AirportEntity airport = getAirportById(airportCommand.getAirportId());

        airport.updateOf(
                airportCommand.getCode(),
                airportCommand.getName(),
                airportCommand.getCity(),
                airportCommand.getCountry()
        );

        airport.updateModificationInfo(userId);

        return AirportResponse.from(airport);
    }

    @Transactional
    @CacheEvict(value = "airports", allEntries = true) // 캐시 무효화
    public void deleteAirport(UUID airportId, UUID userId, String userRole) {
        validateUserRole(userRole);

        AirportEntity airport = getAirportById(airportId);

        airport.updateDeletionInfo(userId);
    }

    private AirportEntity getAirportById(UUID airportId) {
        return airportRepository.findById(airportId)
                .orElseThrow(AirportNotFoundException::new);
    }

    private void getAirportByCode(CreateAirportCommand airportCommand) {
        if (airportRepository.findByCode(airportCommand.getCode()).isPresent()) {
            throw new AirportDuplicatedException();
        }
    }

    private void validateUserRole(String userRole) {
        if (userRole.equals("CUSTOMER")) {
            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
        }
    }
}
