package com.oneul_tanda.reservation_service.config;

import com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event.ReservationHeldEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka 컨슈머 설정을 위한 Spring 설정 클래스
 */
@EnableKafka
@Configuration
public class ReservationKafkaConfig {


    /**
     * ReservationHeldEvent 타입의 Kafka ConsumerFactory 빈 정의
     * - Kafka 컨슈머 인스턴스를 생성하는 팩토리
     * - 메시지의 key는 String, value는 ReservationHeldEvent
     * - JsonDeserializer를 통해 ReservationHeldEvent 역직렬화 처리
     */
    @Bean
    public ConsumerFactory<String, ReservationHeldEvent> reservationHeldConsumerFactory() {

        JsonDeserializer<ReservationHeldEvent> jsonDeserializer = new JsonDeserializer<>(ReservationHeldEvent.class);
        jsonDeserializer.addTrustedPackages("com.oneul_tanda.reservation_service.reservation.infrastructure.kafka.event");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:29092");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                jsonDeserializer
        );
    }


    /**
     * ReservationHeldEvent 타입의 Kafka 리스너 컨테이너 팩토리 빈 정의
     * - @KafkaListener 메서드를 실행할 컨테이너를 생성
     * - 내부적으로 reservationHeldConsumerFactory를 사용하여 컨슈머 생성
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationHeldEvent> reservationHeldListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReservationHeldEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reservationHeldConsumerFactory());
        return factory;
    }



}
