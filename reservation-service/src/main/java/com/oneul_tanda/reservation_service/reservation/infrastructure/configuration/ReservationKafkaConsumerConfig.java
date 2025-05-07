package com.oneul_tanda.reservation_service.reservation.infrastructure.configuration;

import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationHeldEvent;
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
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka 컨슈머 설정을 위한 Spring 설정 클래스
 */
@Slf4j
@EnableKafka
@Configuration
public class ReservationKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    /**
     * Kafka에서 ReservationHeldEvent 메시지를 소비하기 위한 ConsumerFactory 빈 설정
     *
     * - Kafka 메시지의 key는 String, value는 ReservationHeldEvent 클래스.
     * - 역직렬화 오류를 처리하기 위해 ErrorHandlingDeserializer를 래핑하여 사용.
     * - 메시지 역직렬화를 위해 JsonDeserializer를 사용.
     * - TRUSTED_PACKAGES 설정을 통해 역직렬화 허용 패키지를 명시.
     * - KafkaListener에서 사용할 Kafka Consumer 인스턴스를 생성하는 팩토리 역할.
     */
    @Bean
    public ConsumerFactory<String, ReservationHeldEvent> reservationHeldConsumerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
                "com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationHeldEvent");

        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,
                "com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }



    //DLQ용 KafkaTemplate 설정
    @Bean
    public KafkaTemplate<String, byte[]> dlqKafkaTemplate() {

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }



    /**
     * KafkaListener에서 ReservationHeldEvent 메시지를 처리하기 위한 리스너 컨테이너 팩토리 설정
     *
     * - Kafka 메시지를 처리할 리스너 컨테이너를 생성하는 팩토리.
     * - 내부적으로 reservationHeldConsumerFactory를 사용하여 Consumer 인스턴스를 생성.
     * - 메시지 처리 중 예외가 발생하면 DLQ(Dead Letter Queue)로 전송되도록 에러 핸들러를 설정.
     * - @KafkaListener 어노테이션이 붙은 메서드에서 이 팩토리를 통해 Kafka 메시지를 처리.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationHeldEvent> reservationHeldListenerFactory(
            KafkaTemplate<String, byte[]> dlqKafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(dlqKafkaTemplate,
                (record, ex) -> {
                    log.warn("[DLQ] 실패 메시지 전송. key={}, cause={}", record.key(), ex.getMessage());
                    return new TopicPartition("reservation-held-dlq", 0);
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

        ConcurrentKafkaListenerContainerFactory<String, ReservationHeldEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(errorHandler);
        factory.setConsumerFactory(reservationHeldConsumerFactory());
        return factory;
    }



}
