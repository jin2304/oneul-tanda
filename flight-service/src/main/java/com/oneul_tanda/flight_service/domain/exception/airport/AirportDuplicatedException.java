package com.oneul_tanda.flight_service.domain.exception.airport;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;

public class AirportDuplicatedException extends DataIntegrityViolationException {
    public AirportDuplicatedException() {
        super(ErrorMessage.DUPLICATED_AIRPORT.getMessage());
    }
}
