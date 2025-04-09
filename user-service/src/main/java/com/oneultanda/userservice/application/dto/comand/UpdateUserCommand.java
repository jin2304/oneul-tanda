package com.oneultanda.userservice.application.dto.comand;

public record UpdateUserCommand(
        String nickname,
        String email,
        String contact
) {
}