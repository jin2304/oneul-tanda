package com.oneultanda.userservice.presentation.controller;

import com.oneultanda.userservice.application.service.UserService;
import com.oneultanda.userservice.domain.model.Role;
import com.oneultanda.userservice.domain.model.User;
import com.oneultanda.userservice.presentation.dto.request.*;
import com.oneultanda.userservice.presentation.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userservice;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        userservice.registerUser(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 회원가입시 username 중복체크
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUsername(
            @RequestParam String username
    ) {
        boolean isUsername= userservice.checkUsername(username);
        return ResponseEntity.ok(isUsername);
    }

    /**
     * sing-in 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(
            @RequestBody @Valid LoginUserRequest request
    ) {
        String accessToken = userservice.loginUser(request.toCommand());
        return ResponseEntity.ok()
                .header("Authorization", accessToken)
                .build();
    }

    /**
     * todo: 없는 url로 요청이 올때 에러처리가 가능한가? 이부분은 gateway에서 처리할 것으로 예상
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(
            @RequestHeader("X-User-ID") UUID userId
    ) {
        User user = userservice.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody UpdateUserRequest request
    ) {
        URI location = userservice.updateUser(userId, request.toCommand());
        return ResponseEntity.ok().location(location).build();
    }

    /**
     * todo: 시간 여유나면 email 인증 or email로 변경된 비밀번호 or 비빌번호 변경 링크 보내기 형식
     */
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody UpdatePasswordRequest request
    ) {
        userservice.updatePassword(userId, request.toCommand());
        return ResponseEntity.ok().build();
    }
    /**
     * 계정 삭제(소프트 딜리트0
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody DeleteUserRequest request
    ) {
        userservice.deleteUser(userId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    /**
     * todo: 현재 내부에서 권한 확인하고 있지만 이걸 gateway에서 진행해줘야하는가?
     */
    @GetMapping("/admin/{username}")
    public ResponseEntity<UserResponse> getUserFromUsername(
            @RequestHeader("X-User-Role") final Role role,
            @PathVariable final String username
    ) {
        User user = userservice.getUserFromUsername(role, username);
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(response);
    }


    /**
     * username으로 관리자 권한 변경
     */
    @PutMapping("/admin/{username}")
    public ResponseEntity<Void> updateRole(
            @RequestHeader("X-User-Role") final Role role,
            @PathVariable final String username,
            @RequestBody final UpdateUserRoleRequest request
    ) {
        URI location = userservice.updateRole(role, username, request.toCommand());
        return ResponseEntity.ok().location(location).build();
    }

    /**
     * 외부에서는 접근 불가능하도록 해야하는 feign client 전용
     * todo: 1. 분리하기 2. gateway에서 이곳으로의 접근 차단or 없는 url인척하기
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserFromUserId(
            @PathVariable final UUID userId
    ) {
        User user = userservice.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(response);
    }

}
