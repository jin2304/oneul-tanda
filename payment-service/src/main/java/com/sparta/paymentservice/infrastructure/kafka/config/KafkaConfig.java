package com.sparta.paymentservice.infrastructure.kafka.config;

import com.sparta.paymentservice.infrastructure.kafka.event.ReservationCanceledEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ReservationCanceledEvent> producerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        JsonSerializer<ReservationCanceledEvent> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), jsonSerializer);
    }

    @Bean
    public KafkaTemplate<String, ReservationCanceledEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, ReservationCanceledEvent> reservationCanceledConsumerFactory() {

        JsonDeserializer<ReservationCanceledEvent> jsonDeserializer = new JsonDeserializer<>(ReservationCanceledEvent.class);
        
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.sparta.paymentservice.infrastructure.kafka.event.ReservationCanceledEvent");

        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,
                "com.sparta.paymentservice.infrastructure.kafka.event");

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public KafkaTemplate<String, byte[]> dlqKafkaTemplate() {

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationCanceledEvent> reservationCanceledListenerFactory(
            KafkaTemplate<String, byte[]> dlqKafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(dlqKafkaTemplate,
                (record, ex) -> {
                    log.warn("[DLQ] 실패 메시지 전송. key={}, cause={}", record.key(), ex.getMessage());
                    return new TopicPartition("reservation-canceled-dlq", 0);
                });


        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));

        errorHandler.addRetryableExceptions(
                RuntimeException.class,
                DeserializationException.class
        );

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (deliveryAttempt == 1) {
                log.warn("[초기 처리 실패] key={}, value={}, cause={}", record.key(), record.value(), ex.getMessage());
            } else {
                log.warn("[재시도] {}번째 시도 실패 - key: {}, value: {}, exception type: {}, cause={}",
                        deliveryAttempt - 1, record.key(), record.value(),
                        ex.getClass().getSimpleName(), ex.getMessage());
            }
        });

        ConcurrentKafkaListenerContainerFactory<String, ReservationCanceledEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(errorHandler);
        factory.setConsumerFactory(reservationCanceledConsumerFactory());
        return factory;
    }
}
