package com.oneultanda.userservice.infrastructure.mapper;

import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.infrastructure.entity.UserJpaEntity;

public class UserMapper {
    public static UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .contact(user.getContact())
                .role(user.getRole())
                .tokenVersion(user.getTokenVersion())
                .build();
        if (user.isDeleted()) {
            entity.markDeleted(user.getId());
        }
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        return entity.toDomain();
    }
}
