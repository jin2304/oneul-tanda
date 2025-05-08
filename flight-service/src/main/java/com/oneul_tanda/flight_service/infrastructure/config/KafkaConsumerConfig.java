package com.oneul_tanda.flight_service.infrastructure.config;

import com.oneul_tanda.flight_service.application.dtos.event.ReservationCanceledEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // 예약 취소(결제 취소) ConsumerFactory
    @Bean
    public ConsumerFactory<String, ReservationCanceledEvent> paymentCanceledConsumerFactory() {
        JsonDeserializer<ReservationCanceledEvent> valueDeserializer = new JsonDeserializer<>(
                ReservationCanceledEvent.class);
        valueDeserializer.setUseTypeMapperForKey(false);
        valueDeserializer.setRemoveTypeHeaders(true);
        valueDeserializer.addTrustedPackages("com.oneul_tanda.flight_service.application.event");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), valueDeserializer);
    }

    // 예약 취소(결제 취소) ContainerFactory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationCanceledEvent> paymentCanceledListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReservationCanceledEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentCanceledConsumerFactory());
        // 에러 핸들러 설정 (기본적으로 재시도 0, DLQ 구성 가능)
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        return factory;
    }
}