package com.oneul_tanda.reservation_service.passenger.domain.entity;

import com.oneul_tanda.reservation_service.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "m_passengers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Passenger extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "passenger_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "passenger_name", nullable = false)
    String name;

    @Column(name = "birth", nullable = false)
    String birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "passport_number", nullable = false)
    String passportNumber;


    /**
     * 탑승객 생성
     */
    public static Passenger createPassenger(UUID userId, String name, String birth, Gender gender, String passportNumber) {
        Passenger passenger = Passenger.builder()
                .name(name)
                .birth(birth)
                .gender(gender)
                .passportNumber(passportNumber)
                .build();

        passenger.registerCreatedBy(userId);

        return passenger;
    }

}
