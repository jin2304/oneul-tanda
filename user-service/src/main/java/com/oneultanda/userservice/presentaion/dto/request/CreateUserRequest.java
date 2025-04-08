package com.oneultanda.userservice.presentaion.dto.request;

import com.oneultanda.userservice.application.dto.comand.CreateUserCommand;

public record CreateUserRequest(
        String username,
        String password,
        String nickname,
        String email,
        String contact
) {
    public CreateUserCommand toCommand() {
        return new CreateUserCommand(username, password, nickname, email, contact);
    }
}
