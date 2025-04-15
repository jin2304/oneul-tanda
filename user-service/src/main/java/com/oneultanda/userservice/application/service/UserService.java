package com.oneultanda.userservice.application.service;

import com.oneultanda.userservice.application.dto.comand.*;
import com.oneultanda.userservice.common.exception.CustomException;
import com.oneultanda.userservice.domain.entity.Role;
import com.oneultanda.userservice.domain.entity.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import com.oneultanda.userservice.infrastructure.jwt.JwtUtil;
import com.oneultanda.userservice.presentation.dto.response.UserResponse;
import com.oneultanda.userservice.presentation.exception.PresentationErrorCode;
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
    public UserResponse getUser(final UUID userId) {
        User user = checkUser(userId);
        return UserResponse.fromUser(user);
    }

    @Transactional
    public URI updateUser(final UUID userId, final UpdateUserCommand command) {
        User user = checkUser(userId);
        user.updateFromUpdateUserCommand(command);

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();
    }

    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordCommand command) {
        User user = checkUser(userId);
        checkPassword(user, command.currentPassword());
        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        user.updateFromUpdatePasswordCommand(encodedNewPassword);
    }

    @Transactional
    public void deleteUser(UUID userId, DeleteUserCommand command) {
        User user = checkUser(userId);
        checkPassword(user, command.password());
        user.markDeleted(user.getUsername());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserFromUsername(Role role, String username) {
        checkAdmin(role);
        User user = checkUserFromUsername(username);
        return UserResponse.fromUser(user);
    }

    @Transactional
    public URI updateRole(Role role, String username, UpdateUserRoleCommand command) {
        checkAdmin(role);
        User user = checkUserFromUsername(username);
        user.updateRole(command);

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
                new CustomException(PresentationErrorCode.RESOURCE_NOT_FOUND));
    }


    private void checkPassword(final User user, final String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(PresentationErrorCode.INVALID_REQUEST);
        }
    }

    private User checkUserFromUsername(final String username) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() ->
                        new CustomException(PresentationErrorCode.RESOURCE_NOT_FOUND));
        return user;
    }

    private void checkAdmin(final Role role) {
        if(!role.equals(Role.ADMIN)) {
            throw new CustomException(PresentationErrorCode.ACCESS_DENIED);
        }
    }

    /**
     * todo: blacklist 등록필요시마다 진행해야 할 메서드
     */
    private void addBlackList(final User user){

    }
}
