package com.oneul_tanda.flight_service.domain.exception.airline;

import com.oneul_tanda.flight_service.domain.exception.common.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;

public class AirlineDuplicatedException extends DataIntegrityViolationException {
    public AirlineDuplicatedException() {
        super(ErrorMessage.DUPLICATED_AIRLINE.getMessage());
    }
}
