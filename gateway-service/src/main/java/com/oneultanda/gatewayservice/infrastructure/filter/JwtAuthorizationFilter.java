package com.oneultanda.gatewayservice.infrastructure.filter;

import com.oneultanda.gatewayservice.infrastructure.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        //토큰 없이 통과
        if (path.startsWith("/api/v1/users/signup") || path.startsWith("/api/v1/users/login")) {
            log.info("토큰없이 통과");
            return chain.filter(exchange);
        }
        log.info("토큰 검증 시작");
        String tokenValue = jwtUtil.extractToken(exchange);

        if (!StringUtils.hasText(tokenValue) || !jwtUtil.validateToken(tokenValue)) {
            log.error("invalid token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.getUserInfoFromToken(tokenValue);

        String userId = String.valueOf(claims.get("id"));
        String username = String.valueOf(claims.getSubject());
        String role = String.valueOf(claims.get("role"));

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .header("X-User-Name", username)
                .header("X-User-Role", role)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info("토큰 검즘확인: " + username);
        log.info(userId.toString());

        return chain.filter(mutatedExchange);
    }
}