package com.oneultanda.userservice.infrastructure.repository;

import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUsernameAndDeletedAtIsNull(String username);
    Optional<UserJpaEntity> findByIdAndDeletedAtIsNull(UUID userId);
}
