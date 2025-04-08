package com.oneultanda.userservice.presentaion.controller;

import com.oneultanda.userservice.application.service.UserService;
import com.oneultanda.userservice.presentaion.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userservice;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserFromUserId(
            @PathVariable final Long userId
    ) {
        UserResponse response = userservice.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/user-id")
    public ResponseEntity<Long> getUserFromUsername(
            @PathVariable final String username
    ) {
        Long userId = userservice.getUserIdFromUsername(username);
        return ResponseEntity.ok(userId);
    }
}
