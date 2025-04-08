package com.oneultanda.userservice.presentaion.dto.request;

import com.oneultanda.userservice.application.dto.comand.RegisterUserCommand;

public record RegisterUserRequest(
        String username,
        String password,
        String nickname,
        String email,
        String contact
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(this.username, password, nickname, email, contact);
    }
}
