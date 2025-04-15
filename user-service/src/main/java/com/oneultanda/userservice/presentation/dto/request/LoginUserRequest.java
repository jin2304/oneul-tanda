package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.LoginUserCommand;

public record LoginUserRequest(
        String username,
        String password
) {
    public LoginUserCommand toCommand() {
        return new LoginUserCommand(username, password);
    }
}
