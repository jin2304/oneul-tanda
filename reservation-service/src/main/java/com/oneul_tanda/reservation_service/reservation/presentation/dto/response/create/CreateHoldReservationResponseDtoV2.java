package com.oneul_tanda.reservation_service.reservation.presentation.dto.response.create;

import lombok.Builder;

@Builder
public record CreateHoldReservationResponseDtoV2(
        boolean success,
        String message
) implements CreateHoldReservationResponseDto {

    public static CreateHoldReservationResponseDtoV2 Success(){
        return CreateHoldReservationResponseDtoV2.builder()
                .success(true)
                .message("예약 임시 생성 완료")
                .build();
    }


    public static CreateHoldReservationResponseDtoV2 Failed(){
        return CreateHoldReservationResponseDtoV2.builder()
                .success(false)
                .message("예약 임시 생성 실패")
                .build();
    }
}
