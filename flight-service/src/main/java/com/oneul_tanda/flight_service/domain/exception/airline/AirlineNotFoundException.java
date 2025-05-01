package com.oneul_tanda.flight_service.domain.exception.airline;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;

public class AirlineNotFoundException extends EntityNotFoundException {
    public AirlineNotFoundException() {
        super(ErrorMessage.AIRLINE_NOT_FOUND.getMessage());
    }
}
