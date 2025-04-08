package com.oneultanda.userservice.application.service;

import com.oneultanda.userservice.application.dto.comand.RegisterUserCommand;
import com.oneultanda.userservice.application.dto.comand.UpdatePasswordCommand;
import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;
import com.oneultanda.userservice.common.exception.CustomException;
import com.oneultanda.userservice.domain.entity.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
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
        if (!user.getPassword().equals(command.currentPassword())) {
            throw new CustomException(PresentaionErrorCode.INVALID_REQUEST);
        }
        user.updateFromUpdatePasswordCommand(command);
    }

    private User checkUser(final Long userId) {
        return userRespository.findById(userId)
                .orElseThrow(() ->
                new CustomException(PresentaionErrorCode.RESOURCE_NOT_FOUND));
    }


}
