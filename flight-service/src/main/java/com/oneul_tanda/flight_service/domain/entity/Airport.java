package com.oneul_tanda.flight_service.domain.entity;

import com.oneul_tanda.flight_service.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "m_airports")
@Entity
public class Airport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "airport_name", nullable = false)
    private String name;

    @Column(name = "airport_code", nullable = false)
    private String code;

    @Column(name = "airport_city", nullable = false)
    private String city;

    @Column(name = "airport_country", nullable = false)
    private String country;

    public static Airport from(String code, String name, String city, String country) {
        return Airport.builder()
                .code(code)
                .name(name)
                .city(city)
                .country(country)
                .build();
    }
}
