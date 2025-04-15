package com.oneul_tanda.flight_service.application.service.airport;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.domain.entity.Airport;
import com.oneul_tanda.flight_service.domain.repository.airport.AirportRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportExternalService {

    private final Amadeus amadeus;
    private final AirportRepository airportRepository;

    // 실시간 공항 정보 조회
    public Location[] searchAirports(String keyword) throws Exception {
        Params params = Params.with("subType", "AIRPORT")
                .and("keyword", keyword)
                .and("page[limit]", "10")
                .and("sort", "analytics.travelers.score");

        return amadeus.referenceData.locations.get(params);
    }

    // 실시간 공항 정보 조회 및 DB 저장
    public List<AirportResponse> searchAndSaveAirports(String keyword) throws Exception {
        Location[] locations = amadeus.referenceData.locations
                .get(Params.with("keyword", keyword).and("subType", "AIRPORT"));

        return Arrays.stream(locations)
                .map(location -> {
                    String code = location.getIataCode();

                    // 중복된 공항은 저장하지 않음
                    Airport airport = airportRepository.findByCode(code).orElseGet(() -> {
                        Airport newAirport = Airport.from(
                                code,
                                location.getName(),
                                location.getAddress().getCityName(),
                                location.getAddress().getCountryName());
                        return airportRepository.save(newAirport);
                    });

                    return AirportResponse.from(airport);
                })
                .toList();
    }

}
