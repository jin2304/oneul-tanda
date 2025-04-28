package com.oneul_tanda.flight_service.domain.exception.airport;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;

public class AirportNotFoundException extends EntityNotFoundException {
    public AirportNotFoundException() {
        super(ErrorMessage.AIRPORT_NOT_FOUND.getMessage());
    }
}
