package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdatePasswordCommand;

public record UpdatePasswordRequest(
        String currentPassword,
        String newPassword
) {
    public UpdatePasswordCommand toCommand() {
        return new UpdatePasswordCommand(currentPassword, newPassword);
    }
}
