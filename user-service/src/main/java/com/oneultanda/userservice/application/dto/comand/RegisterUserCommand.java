package com.oneultanda.userservice.application.dto.comand;

import com.oneultanda.userservice.domain.entity.User;

public record RegisterUserCommand(
        String username,
        String password,
        String nickname,
        String email,
        String contact
) {
    public User toEntity() {
        return User.from(username, password, nickname, email, contact);
    }
}