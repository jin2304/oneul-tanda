package com.oneultanda.userservice.application.dto.comand;

public record LoginUserCommand(
        String username,
        String password
) {
}