package com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update;

import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ConfirmReservationRequestDtoV2(

        @NotNull(message = "항공편 ID는 필수입니다.")
        UUID flightId,

        @Valid List<ConfirmPassengerDtoV2> passengers
) {

    public record ConfirmPassengerDtoV2(

            String name,

            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
            String birth,

            Gender gender,

            @Size(min = 6, max = 10, message = "여권 번호는 6자 이상 10자 이하로 입력하세요.")
            @Pattern(regexp = "[A-Z0-9]+", message = "여권 번호는 대문자와 숫자만 입력 가능합니다.")
            String passportNumber
    ) {}
}

