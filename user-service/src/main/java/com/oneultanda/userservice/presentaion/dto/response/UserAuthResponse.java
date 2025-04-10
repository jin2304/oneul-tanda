package com.oneultanda.userservice.presentaion.dto.response;

import com.oneultanda.userservice.domain.entity.User;

public record UserAuthResponse (
        Long id,
        String username,
        String encodedPassword
) {
    public static UserAuthResponse fromUser(User user) {
        return new UserAuthResponse(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
