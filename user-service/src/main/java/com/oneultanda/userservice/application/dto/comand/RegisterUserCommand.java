package com.oneultanda.userservice.application.dto.comand;

public record RegisterUserCommand(
        String username,
        String password,
        String nickname,
        String email,
        String contact
) {
}