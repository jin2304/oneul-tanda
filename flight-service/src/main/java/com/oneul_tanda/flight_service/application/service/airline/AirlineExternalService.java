package com.oneul_tanda.flight_service.application.service.airline;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Airline;
import com.oneul_tanda.flight_service.application.service.CacheService;
import com.oneul_tanda.flight_service.domain.entity.AirlineEntity;
import com.oneul_tanda.flight_service.domain.repository.airline.AirlineRepository;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineSearchResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineExternalService {

    private final Amadeus amadeus;
    private final AirlineRepository airlineRepository;
    private final CacheService cacheService;

    // 실시간 항공사 정보 조회
    public List<AirlineSearchResponse> searchAirlines(String keyword) throws Exception {
        Airline[] amadeusAirlines = amadeus.referenceData.airlines
                .get(Params.with("airlineCodes", keyword));

        return Arrays.stream(amadeusAirlines)
                .map(amadeusAirline -> new AirlineSearchResponse(
                        amadeusAirline.getIataCode(),
                        amadeusAirline.getCommonName()))
                .collect(Collectors.toList());
    }

    // 실시간 항공사 정보 조회 및 DB 저장
    public List<AirlineResponse> searchAndSaveAirlines(String keyword) throws Exception {
        Airline[] amadeusAirlines = amadeus.referenceData.airlines
                .get(Params.with("airlineCodes", keyword));

        return Arrays.stream(amadeusAirlines)
                .map(amadeusAirline -> {
                    String code = amadeusAirline.getIataCode(); // IATA 코드
                    String name = amadeusAirline.getCommonName(); // 항공사 이름

                    // 중복된 항공사는 저장하지 않음
                    AirlineEntity existingAirline = airlineRepository.findByCode(code).orElseGet(() -> {
                        AirlineEntity newAirline = AirlineEntity.from(
                                code, name);
                        return airlineRepository.save(newAirline);
                    });

                    return cacheService.cacheAirline(AirlineResponse.from(existingAirline));
                })
                .toList();
    }

}
