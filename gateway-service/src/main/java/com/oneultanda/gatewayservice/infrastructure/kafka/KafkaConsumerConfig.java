package com.oneultanda.gatewayservice.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Bean
    public ConsumerFactory<UUID, TokenVersionChangedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // ✅ Deserializer를 ErrorHandlingDeserializer로 감싸줌
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // ✅ 실제 사용할 Deserializer 지정
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, UUIDDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // ✅ JsonDeserializer 설정
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "TokenVersionChangedEvent:com.oneultanda.gatewayservice.infrastructure.kafka.TokenVersionChangedEvent");

        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<UUID, TokenVersionChangedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, TokenVersionChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
