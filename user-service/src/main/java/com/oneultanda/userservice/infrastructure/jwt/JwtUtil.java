package com.oneultanda.userservice.infrastructure.jwt;

import com.oneultanda.userservice.domain.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final String issuer;
    private final Long accessExpiration;
    private final Long refreshExpiration;
    private final SecureDigestAlgorithm signatureAlgorithm;
    // todo: 당장은 더 간단한 hsAlgorithm 사용, 추후 rs 고려할것

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-expiration}") Long accessExpiration,
            @Value("${jwt.refresh-expiration}") Long refreshExpiration
    ) {
        log.info("-------------------------------------------" + secret);
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.signatureAlgorithm = Jwts.SIG.HS256;
    }

    public String createAccessToken(String username, Role role, UUID id, int tokenVersion) {
        Map<String, Object> claims = Map.of(
                "role", role.toString(),
                "id", id.toString(),
                "tokenVersion", tokenVersion
        );

        return generateToken(Duration.ofMinutes(accessExpiration), username, claims);
    }

    //todo: refresh는 틀만 작성
    public String createRefreshToken(Long id) {

        return generateToken(Duration.ofDays(refreshExpiration), null, Map.of("id", id));
    }

    // 토큰 생성
    private String generateToken(Duration expiration, String username, Map<String, Object> claims) {
        // aws 의 리전이 어디에 생길지 모르니 타임 존을 명확하게 ...
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info(secretKey.toString());
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuer(issuer)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(now.plus(expiration).toInstant()))
                .signWith(secretKey, signatureAlgorithm)
                .compact();
    }
}
