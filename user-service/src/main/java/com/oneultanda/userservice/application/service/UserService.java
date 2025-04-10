package com.oneultanda.userservice.application.service;

import com.oneultanda.userservice.application.dto.comand.*;
import com.oneultanda.userservice.common.exception.CustomException;
import com.oneultanda.userservice.domain.entity.Role;
import com.oneultanda.userservice.domain.entity.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import com.oneultanda.userservice.presentaion.dto.response.UserAuthResponse;
import com.oneultanda.userservice.presentaion.dto.response.UserResponse;
import com.oneultanda.userservice.presentaion.exception.PresentaionErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRespository;


    @Transactional
    public void registerUser(final RegisterUserCommand command) {
        User user = command.toUser();
        userRespository.save(user);
    }

    /**
     * todo: 추후 만일 cqrs를 제대로 적용시 조회 / 생성,수정,삭제 db를 분리하고 각 서비스를 handler로 분리할 수 있음
     */
    @Transactional(readOnly = true)
    public UserResponse getUser(final Long userId) {
        User user = checkUser(userId);
        return UserResponse.fromUser(user);
    }

    @Transactional
    public URI updateUser(final Long userId, final UpdateUserCommand command) {
        User user = checkUser(userId);
        user.updateFromUpdateUserCommand(command);

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordCommand command) {
        User user = checkUser(userId);
        checkPassword(user, command.currentPassword());
        user.updateFromUpdatePasswordCommand(command);
    }

    @Transactional
    public void deleteUser(Long userId, DeleteUserCommand command) {
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

    @Transactional(readOnly = true)
    public UserAuthResponse getUserIdFromUsername(String username) {
        User user = checkUserFromUsername(username);
        return UserAuthResponse.fromUser(user);
    }

    /**
     * todo: gateway에서 검증된 값이므로 굳이 한번더 check 할 필요가 없나?
     */
    private User checkUser(final Long userId) {
        return userRespository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() ->
                new CustomException(PresentaionErrorCode.RESOURCE_NOT_FOUND));
    }

    private void checkPassword(final User user, final String password) {
        if (!user.getPassword().equals(password)) {
            throw new CustomException(PresentaionErrorCode.INVALID_REQUEST);
        }
    }

    private User checkUserFromUsername(final String username) {
        User user = userRespository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() ->
                        new CustomException(PresentaionErrorCode.RESOURCE_NOT_FOUND));
        return user;
    }

    private void checkAdmin(final Role role) {
        if(!role.equals(Role.ADMIN)) {
            throw new CustomException(PresentaionErrorCode.ACCESS_DENIED);
        }
    }
}
