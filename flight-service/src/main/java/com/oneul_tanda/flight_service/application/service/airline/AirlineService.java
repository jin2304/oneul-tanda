package com.oneul_tanda.flight_service.application.service.airline;

import com.oneul_tanda.flight_service.application.dtos.airline.CreateAirlineCommand;
import com.oneul_tanda.flight_service.application.dtos.airline.UpdateAirlineCommand;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.exception.airline.AirlineDuplicatedException;
import com.oneul_tanda.flight_service.domain.exception.airline.AirlineNotFoundException;
import com.oneul_tanda.flight_service.domain.exception.common.GlobalException;
import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
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
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Cacheable(value = "airlines", key = "#airlineId")
    public AirlineResponse getAirline(UUID airlineId, String userRole) {
        validateUserRole(userRole);

        AirlineEntity airline = getAirlineById(airlineId);

        return AirlineResponse.from(airline);
    }

    public Page<AirlineResponse> searchAirlines(String code, String name, Pageable pageable, String userRole) {
        validateUserRole(userRole);

        Pageable adjusted = PagingUtil.adjustPageable(pageable);
        Page<AirlineEntity> airlines = airlineRepository.findByCodeAndName(code, name, adjusted);

        return airlines.map(AirlineResponse::from);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public AirlineResponse createAirline(CreateAirlineCommand airlineCommand, UUID userId, String userRole) {
        validateUserRole(userRole);

        getAirlineByCode(airlineCommand);

        AirlineEntity airline = AirlineEntity.from(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );

        airlineRepository.save(airline);
        airline.updateCreationInfo(userId);

        return AirlineResponse.from(airline);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public AirlineResponse updateAirline(UpdateAirlineCommand airlineCommand, UUID userId, String userRole) {
        validateUserRole(userRole);

        AirlineEntity airline = getAirlineById(airlineCommand.getAirlineId());

        airline.updateOf(
                airlineCommand.getCode(),
                airlineCommand.getName()
        );

        airline.updateModificationInfo(userId);

        return AirlineResponse.from(airline);
    }

    @Transactional
    @CacheEvict(value = "airlines", allEntries = true) // 캐시 무효화
    public void deleteAirline(UUID airlineId, UUID userId, String userRole) {
        validateUserRole(userRole);

        AirlineEntity airline = getAirlineById(airlineId);

        airline.updateDeletionInfo(userId);
    }

    private AirlineEntity getAirlineById(UUID airlineId) {
        return airlineRepository.findById(airlineId)
                .orElseThrow(AirlineNotFoundException::new);
    }

    private void getAirlineByCode(CreateAirlineCommand airlineCommand) {
        if (airlineRepository.findByCode(airlineCommand.getCode()).isPresent()) {
            throw new AirlineDuplicatedException();
        }
    }

    private void validateUserRole(String userRole) {
        if (userRole.equals("CUSTOMER")) {
            throw new GlobalException(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
        }
    }
}
