package com.oneul_tanda.reservation_service.passenger.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "m_passengers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "passenger_id", updatable = false, nullable = false)
    private UUID id;

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
    public static Passenger createPassenger(String birth, Gender gender, String passportNumber) {
        return Passenger.builder()
                .birth(birth)
                .gender(gender)
                .passportNumber(passportNumber)
                .build();
    }

}
