package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.LoginUserCommand;
import jakarta.validation.constraints.NotNull;

public record LoginUserRequest(

        @NotNull(message = "계정명은 필수입니다.")
        String username,
        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
    public LoginUserCommand toCommand() {
        return new LoginUserCommand(username, password);
    }
}
