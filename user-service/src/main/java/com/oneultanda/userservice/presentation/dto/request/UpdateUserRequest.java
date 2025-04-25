package com.oneultanda.userservice.presentation.dto.request;

import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname,

        @Email(message = "유효한 이메일 주소를 입력하세요.")
        String email,

        @Pattern(
                regexp = "^(01[0-9]-\\d{3,4}-\\d{4})$|^(0\\d{1,2}-\\d{3,4}-\\d{4})$",
                message = "올바른 전화번호 형식을 입력하세요. 예: 010-1234-5678 또는 02-123-4567"
        )
        String contact
) {
    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(nickname, email, contact);
    }
}
