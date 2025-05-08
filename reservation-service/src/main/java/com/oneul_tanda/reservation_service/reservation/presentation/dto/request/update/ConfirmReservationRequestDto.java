package com.oneul_tanda.reservation_service.reservation.presentation.dto.request.update;

import com.oneul_tanda.reservation_service.reservation.domain.entity.vo.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ConfirmReservationRequestDto(

        @NotNull(message = "하나 이상의 티켓 정보를 입력해야 합니다.")
        @Size(min = 1, message = "최소 하나 이상의 티켓 정보가 필요합니다.")
        @Valid List<ConfirmTicketDto> tickets
) {

    public record ConfirmTicketDto(
            @NotNull(message = "티켓 ID는 필수입니다.")
            UUID ticketId,

            @NotNull(message = "탑승객 정보는 필수입니다.")
            @Valid ConfirmPassengerDto passenger
    ) {}

    public record ConfirmPassengerDto(

            @NotBlank(message = "이름은 필수입니다.")
            String name,

            @NotBlank(message = "생년월일은 필수입니다.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
            String birth,

            @NotNull(message = "성별은 필수입니다.")
            Gender gender,

            @NotBlank(message = "여권 번호는 필수입니다.")
            @Size(min = 6, max = 10, message = "여권 번호는 6자 이상 10자 이하로 입력하세요.")
            @Pattern(regexp = "[A-Z0-9]+", message = "여권 번호는 대문자와 숫자만 입력 가능합니다.")
            String passportNumber
    ) {}
}

