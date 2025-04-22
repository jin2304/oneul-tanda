package com.oneultanda.userservice.infrastructure.entity;

import com.oneultanda.userservice.domain.model.Role;
import com.oneultanda.userservice.domain.model.User;
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
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false)
    private int tokenVersion;

    @Builder
    private UserJpaEntity(UUID id, String username, String password, String nickname, String email, String contact, Role role, int tokenVersion) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.contact = contact;
        this.role = role;
        this.tokenVersion = tokenVersion;
    }

    public static UserJpaEntity create(String username, String encodedPassword, String nickname, String email, String contact) {
        UserJpaEntity userJpaEntity = UserJpaEntity.builder()

                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .email(email)
                .contact(contact)
                .role(Role.CUSTOMER)
                .tokenVersion(1)
                .build();

        return userJpaEntity;
    }

    public User toDomain() {
        return com.oneultanda.userservice.domain.model.User.of(
                this.id,
                this.username,
                this.password,
                this.nickname,
                this.email,
                this.contact,
                this.role,
                this.tokenVersion
        );
    }
}