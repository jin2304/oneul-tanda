package com.oneultanda.userservice.presentaion.dto.response;

public record UserResponse(
        String username,
        String nickname,
        String email,
        String contact
) {
}
