package com.oneultanda.userservice.application.service;

import com.oneultanda.userservice.application.dto.comand.*;
import com.oneultanda.userservice.application.exception.ApplicationErrorCode;
import com.oneultanda.userservice.domain.model.Role;
import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import com.oneultanda.userservice.infrastructure.jwt.JwtUtil;
import com.oneultanda.userservice.common.exception.CustomException;
import com.oneultanda.userservice.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void registerUser(final RegisterUserCommand command) {
        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.create(
                command.username(),
                encodedPassword,
                command.nickname(),
                command.email(),
                command.contact()
        );
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String loginUser(LoginUserCommand command) {
        User user = checkUserFromUsername(command.username());
        checkPassword(user, command.password());
        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole(), user.getId());
        return accessToken;
    }

    /**
     * todo: 추후 만일 cqrs를 제대로 적용시 조회 / 생성,수정,삭제 db를 분리하고 각 서비스를 handler로 분리할 수 있음
     */
    @Transactional(readOnly = true)
    public User getUser(final UUID userId) {
        return checkUser(userId);
    }

    @Transactional
    public URI updateUser(final UUID userId, final UpdateUserCommand command) {
        User user = checkUser(userId);
        user.updateFromUpdateUserCommand(command.nickname(), command.email(), command.contact());
        // 분리로 인해 더티체킹이 안되어 직접 명시해줘야함
        userRepository.save(user);

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();
    }

    // 기존에 발급되어있던 토큰들을 무효화 처리할 필요가 있다.
    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordCommand command) {
        User user = checkUser(userId);
        checkPassword(user, command.currentPassword());
        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        user.updateFromUpdatePasswordCommand(encodedNewPassword);
        // 분리로 인해 더티체킹이 안되어 직접 명시해줘야함
        userRepository.save(user);

        kafkaProducerService.sendTokenVersionChange(userId, user.getTokenVersion());
    }

    @Transactional
    public void deleteUser(UUID userId, DeleteUserCommand command) {
        User user = checkUser(userId);
        checkPassword(user, command.password());
        user.deleteUser();
        // 분리로 인해 더티체킹이 안되어 직접 명시해줘야함
        userRepository.save(user);

        kafkaProducerService.sendTokenVersionChange(userId, user.getTokenVersion());
    }

    @Transactional(readOnly = true)
    public User getUserFromUsername(Role role, String username) {
        checkAdmin(role);
        return checkUserFromUsername(username);
    }

    @Transactional
    public URI updateRole(Role role, String username, UpdateUserRoleCommand command) {
        checkAdmin(role);
        User user = checkUserFromUsername(username);
        user.updateRole(command.role());
        // 분리로 인해 더티체킹이 안되어 직접 명시해줘야함
        userRepository.save(user);

        kafkaProducerService.sendTokenVersionChange(user.getId(), user.getTokenVersion());

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();
    }


    /**
     * todo: gateway에서 검증된 값이므로 굳이 한번더 check 할 필요가 없나?
     */
    private User checkUser(final UUID userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() ->
                new CustomException(ApplicationErrorCode.RESOURCE_NOT_FOUND));
    }


    private void checkPassword(final User user, final String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ApplicationErrorCode.INVALID_REQUEST);
        }
    }

    private User checkUserFromUsername(final String username) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() ->
                        new CustomException(ApplicationErrorCode.RESOURCE_NOT_FOUND));
        return user;
    }

    private void checkAdmin(final Role role) {
        if(!role.equals(Role.ADMIN)) {
            throw new CustomException(ApplicationErrorCode.ACCESS_DENIED);
        }
    }
}
