package com.oneultanda.userservice.domain.repository;

import com.oneultanda.userservice.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User gathering);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByIdAndDeletedAtIsNull(UUID userId);
}
