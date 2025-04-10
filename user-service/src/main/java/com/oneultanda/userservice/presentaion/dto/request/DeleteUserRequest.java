package com.oneultanda.userservice.presentaion.dto.request;

import com.oneultanda.userservice.application.dto.comand.DeleteUserCommand;
import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;

public record DeleteUserRequest(
        String password
) {
    public DeleteUserCommand toCommand() {
        return new DeleteUserCommand(password);
    }
}
