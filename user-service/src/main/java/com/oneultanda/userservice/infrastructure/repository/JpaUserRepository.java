package com.oneultanda.userservice.infrastructure.repository;

import com.oneultanda.userservice.domain.entity.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
}
