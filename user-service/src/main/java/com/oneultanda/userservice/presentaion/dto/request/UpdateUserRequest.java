package com.oneultanda.userservice.presentaion.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;

public record UpdateUserRequest(
        String nickname,
        String email,
        String contact
) {
    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(nickname, email, contact);
    }
}
