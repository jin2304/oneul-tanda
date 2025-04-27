package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdatePasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
        @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
        String newPassword
) {
    public UpdatePasswordCommand toCommand() {
        return new UpdatePasswordCommand(currentPassword, newPassword);
    }
}
