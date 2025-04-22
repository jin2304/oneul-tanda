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
     * todo: auth를 통해 암호화 등의 작업을 거친뒤 단순 저장 작업을 여기서 진행
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        userservice.registerUser(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
                .header("Authorization", "Bearer " + accessToken)
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
     * todo: 비밀번호 변경시 로그아웃 처리 필요(blacklist 등록) - auth에서 진행
     * todo: auth에서 암호화까지 거친뒤 값을 받아옴
     * todo: 추후 feignclient grpc를 통해 수정 작업 진행
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
     * todo: 비밀번호 변경과 동일한 과정 필요
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
     * todo: 관리자 대상 (get, seardh)(username 기반) gateway에서 토큰처리 함에 있어서도 필요함
     * todo: gateway에서 권한 있는 사람만 사용가능하게 설정 + id값 필요시 feignclient 요청
     * todo: gateway에서 권한 있는 사람만 사용가능하게 설정 + gateway에서 feignclient 요청시 header에 권한담아서 요청 + 내부에서 권한 검증 진행
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

    @PutMapping("/admin/{username}")
    public ResponseEntity<Void> updateRole(
            @RequestHeader("X-User-Role") final Role role,
            @PathVariable final String username,
            @RequestBody final UpdateUserRoleRequest request
    ) {
        URI location = userservice.updateRole(role, username, request.toCommand());
        return ResponseEntity.ok().location(location).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserFromUserId(
            @PathVariable final UUID userId
    ) {
        User user = userservice.getUser(userId);
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(response);
    }

}
