package com.oneultanda.userservice.infrastructure.repository;

import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import com.oneultanda.userservice.infrastructure.entity.UserJpaEntity;
import com.oneultanda.userservice.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity userJpaEntity = UserMapper.toJpaEntity(user);
        userJpaEntity = userJpaRepository.save(userJpaEntity);
        return UserMapper.toDomain(userJpaEntity);
    }

    @Override
    public Optional<User> findByUsernameAndDeletedAtIsNull(String username) {
        Optional<UserJpaEntity> userJpaEntity = userJpaRepository.findByUsernameAndDeletedAtIsNull(username);
        return userJpaEntity.map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByIdAndDeletedAtIsNull(UUID userId) {
        Optional<UserJpaEntity> userJpaEntity = userJpaRepository.findByIdAndDeletedAtIsNull(userId);
        return userJpaEntity.map(UserMapper::toDomain);
    }
}
