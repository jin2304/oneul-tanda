package com.oneultanda.userservice.domain.repository;

import com.oneultanda.userservice.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User gathering);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByIdAndDeletedAtIsNull(Long userId);
}
