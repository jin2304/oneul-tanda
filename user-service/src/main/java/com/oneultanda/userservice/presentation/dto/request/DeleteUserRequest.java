package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.DeleteUserCommand;

public record DeleteUserRequest(
        String password
) {
    public DeleteUserCommand toCommand() {
        return new DeleteUserCommand(password);
    }
}
