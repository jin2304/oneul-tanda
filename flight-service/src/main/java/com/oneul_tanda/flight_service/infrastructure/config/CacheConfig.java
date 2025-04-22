package com.oneul_tanda.flight_service.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneul_tanda.flight_service.presentation.dtos.airline.AirlineResponse;
import com.oneul_tanda.flight_service.presentation.dtos.airport.AirportResponse;
import com.oneul_tanda.flight_service.presentation.dtos.flight.FlightDetailResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // 기본 직렬화기 (다른 캐시용)
        GenericJackson2JsonRedisSerializer genericSerializer = new GenericJackson2JsonRedisSerializer(
                objectMapper);

        // 특정 DTO용 직렬화기
        Jackson2JsonRedisSerializer<AirportResponse> airportSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper, AirportResponse.class);
        Jackson2JsonRedisSerializer<AirlineResponse> airlineSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper, AirlineResponse.class);
        Jackson2JsonRedisSerializer<FlightDetailResponse> flightSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper, FlightDetailResponse.class);

        // 기본 캐시 설정
        RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericSerializer))
                .disableCachingNullValues();

        // airports 캐시 설정 (12시간 TTL)
        RedisCacheConfiguration airportConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(airportSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(12));

        // airlines 캐시 설정 (12시간 TTL)
        RedisCacheConfiguration airlineConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(airlineSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(12));

        // flights 캐시 설정 (10초 TTL)
        RedisCacheConfiguration flightConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(flightSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(10));

        // flightOffers 캐시 설정 (1분 TTL)
        RedisCacheConfiguration flightOffersConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(flightSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(1));

        // 캐시별 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("flightOffers", flightOffersConfig);
        cacheConfigurations.put("flights", flightConfig);
        cacheConfigurations.put("airlines", airlineConfig);
        cacheConfigurations.put("airports", airportConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(baseConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory, ObjectMapper objectMapper
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }
}
