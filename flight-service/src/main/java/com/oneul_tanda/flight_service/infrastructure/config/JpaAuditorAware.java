package com.oneul_tanda.flight_service.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class JpaAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        // 현재 요청의 RequestAttributes 를 가져옴
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            try {
                return Optional.ofNullable(userId)
                        .map(UUID::fromString); // 명시적으로 변환
            } catch (IllegalArgumentException e) {
                // UUID 파싱 실패한 경우
                log.error("Invalid UUID format: {}", userId, e);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}