package com.oneultanda.userservice.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User{
    private UUID id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String contact;
    private Role role;
    private int tokenVersion;
    private boolean isDeleted;

    @Builder
    private User(UUID id, String username, String password, String nickname, String email, String contact,
                 Role role, int tokenVersion, boolean isDeleted)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.contact = contact;
        this.role = role;
        this.tokenVersion = tokenVersion;
        this.isDeleted = isDeleted;
    }

    public static User create(String username, String encodedPassword, String nickname, String email, String contact) {
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .email(email)
                .contact(contact)
                .role(Role.CUSTOMER)
                .tokenVersion(1)
                .isDeleted(false)
                .build();

        return user;
    }

    public static User of(UUID id, String username, String encodedPassword, String nickname, String email, String contact,
                          Role role, int tokenVersion)
    {
        User user = User.builder()
                .id(id)
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .email(email)
                .contact(contact)
                .role(role)
                .tokenVersion(tokenVersion)
                .build();

        return user;
    }

    public void updateFromUpdateUserCommand(String nickname, String email, String contact) {
        this.nickname = nickname;
        this.email = email;
        this.contact = contact;
    }

    public void updateFromUpdatePasswordCommand(String encodedNewPassword) {
        this.password = encodedNewPassword;
        this.tokenVersion++;
    }

    public void updateRole(Role role) {
        this.role = role;
        this.tokenVersion++;
    }

    public void deleteUser() {
        this.isDeleted = true;
        this.tokenVersion++;
    }
}
