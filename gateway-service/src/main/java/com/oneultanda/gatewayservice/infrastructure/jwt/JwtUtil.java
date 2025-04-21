package com.oneultanda.gatewayservice.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            RedisTemplate<String, String> redisTemplate) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.redisTemplate = redisTemplate;
    }

    public String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)  // 키 설정
                    .build()
                    .parseSignedClaims(token);
            // parseClaimsJws → parseSignedClaims

            String userId = String.valueOf(claims.getPayload().get("id"));
            int tokenVersionInToken = claims.getPayload().get("tokenVersion", Integer.class);

            String key = "token_version:" + userId;
            Integer cachedVersion = Optional.ofNullable(redisTemplate.opsForValue().get(key))
                    .map(Integer::parseInt)
                    .orElse(null); // orElse(0) 도 가능


            if (cachedVersion != null) {
                // Redis에 없으면 -> 통과 처리 있으면 -> 같은 값인지 확인
                // version 변경시 저장되는 redis값의 expire를 token의 유효기간보다 살짝 길게 설정
                // 토큰이 남아있는 동안에만 블랙리스트 처리하면 되도록 보장
                return cachedVersion == tokenVersionInToken;
            }

            return true;

        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // getBody() → getPayload()
    }
}
