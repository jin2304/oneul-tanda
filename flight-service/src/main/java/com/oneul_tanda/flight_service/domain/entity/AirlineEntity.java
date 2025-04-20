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

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_airlines")
@Builder(access = AccessLevel.PRIVATE)
public class AirlineEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "airline_code", nullable = false)
    private String code;

    @Column(name = "airline_name", nullable = false)
    private String name;

    public static AirlineEntity from(String code, String name) {
        return AirlineEntity.builder()
                .code(code)
                .name(name)
                .build();
    }

    public void updateOf(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
