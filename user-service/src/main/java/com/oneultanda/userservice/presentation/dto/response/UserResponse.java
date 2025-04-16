package com.oneultanda.userservice.presentation.dto.response;

import com.oneultanda.userservice.domain.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        String nickname,
        String email,
        String contact
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getContact()
        );
    }
}
