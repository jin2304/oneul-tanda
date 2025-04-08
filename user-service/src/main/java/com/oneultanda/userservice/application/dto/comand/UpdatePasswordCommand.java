package com.oneultanda.userservice.application.dto.comand;

public record UpdatePasswordCommand(
        String currentPassword,
        String newPassword
) {
}
