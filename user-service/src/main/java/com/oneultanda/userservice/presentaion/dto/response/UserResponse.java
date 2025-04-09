package com.oneultanda.userservice.presentaion.dto.response;

import com.oneultanda.userservice.domain.entity.User;

public record UserResponse(
        String username,
        String nickname,
        String email,
        String contact
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getContact()
        );
    }
}
