package com.oneul_tanda.flight_service.infrastructure.config;

import com.amadeus.Amadeus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmadeusConfig {

    @Value("${amadeus.client-id}")
    private String clientId;

    @Value("${amadeus.client-secret}")
    private String clientSecret;

    @Bean
    public Amadeus amadeus() {
        return Amadeus.builder(clientId, clientSecret).build();
    }

}
