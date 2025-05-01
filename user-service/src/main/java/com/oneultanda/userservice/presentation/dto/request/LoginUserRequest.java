package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.LoginUserCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(

        @NotBlank(message = "계정명은 필수 입력값입니다.")
        String username,
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
    public LoginUserCommand toCommand() {
        return new LoginUserCommand(username, password);
    }
}
