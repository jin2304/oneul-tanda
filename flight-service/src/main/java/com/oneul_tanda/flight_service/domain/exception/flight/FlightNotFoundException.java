package com.oneul_tanda.flight_service.domain.exception.flight;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;

public class FlightNotFoundException extends EntityNotFoundException {
    public FlightNotFoundException() {
        super(ErrorMessage.FLIGHT_NOT_FOUND.getMessage());
    }
}
