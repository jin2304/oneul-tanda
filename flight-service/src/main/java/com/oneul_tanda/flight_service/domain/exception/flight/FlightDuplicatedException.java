package com.oneul_tanda.flight_service.domain.exception.flight;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;

public class FlightDuplicatedException extends DataIntegrityViolationException {
    public FlightDuplicatedException() {
        super(ErrorMessage.DUPLICATED_FLIGHT.getMessage());
    }
}
