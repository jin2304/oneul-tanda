package com.oneultanda.userservice.domain.entity;

import com.oneultanda.userservice.application.dto.comand.UpdatePasswordCommand;
import com.oneultanda.userservice.application.dto.comand.UpdateUserCommand;
import com.oneultanda.userservice.application.dto.comand.UpdateUserRoleCommand;
import com.oneultanda.userservice.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "m_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private User(String username, String password, String nickname, String email, String contact, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.contact = contact;
        this.role = role;
    }

    public static User create(String username, String encodedPassword, String nickname, String email, String contact) {
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .email(email)
                .contact(contact)
                .role(Role.CUSTOMER)
                .build();

        return user;
    }

    public void updateFromUpdateUserCommand(UpdateUserCommand command) {
        this.nickname = command.nickname();
        this.email = command.email();
        this.contact = command.contact();
    }

    public void updateFromUpdatePasswordCommand(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

    public void updateRole(UpdateUserRoleCommand command) {
        this.role = command.role();
    }
}
