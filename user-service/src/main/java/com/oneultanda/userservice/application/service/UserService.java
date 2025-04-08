package com.oneultanda.userservice.application.service;

import com.oneultanda.userservice.application.dto.comand.RegisterUserCommand;
import com.oneultanda.userservice.domain.entity.User;
import com.oneultanda.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRespository;


    @Transactional
    public void createUser(RegisterUserCommand command) {
        User user = command.toEntity();
        userRespository.save(user);
    }
}
