package com.oneultanda.userservice.presentation.controller;

import com.oneultanda.userservice.application.service.UserService;
import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userservice;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserFromUserId(
            @PathVariable final UUID userId
    ) {
        User user = userservice.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(response);
    }

}
