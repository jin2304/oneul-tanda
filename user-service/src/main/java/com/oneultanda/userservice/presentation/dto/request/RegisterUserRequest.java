package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterUserRequest(
        @NotNull(message = "계정명은 필수입니다.")
        String username,

        @NotNull(message = "비밀번호는 필수입니다.")
        String password,

        @NotNull(message = "닉네임은 필수입니다.")
        String nickname,

        @Email(message = "유효한 이메일 주소를 입력하세요.")
        String email,

        @NotNull(message = "연락처는 필수입니다.")
        @Pattern(
                regexp = "^(01[0-9]-\\d{3,4}-\\d{4})$|^(0\\d{1,2}-\\d{3,4}-\\d{4})$",
                message = "올바른 전화번호 형식을 입력하세요. 예: 010-1234-5678 또는 02-123-4567"
        )

        String contact
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, password, nickname, email, contact);
    }
}
