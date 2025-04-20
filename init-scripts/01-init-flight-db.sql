-- 1. 관리자 사용자 생성
CREATE ROLE admin_user WITH LOGIN PASSWORD 'admin_password';
ALTER ROLE admin_user CREATEDB CREATEROLE SUPERUSER;

-- 2. flight_db 데이터베이스에 admin_user에게 권한 부여
GRANT ALL PRIVILEGES ON DATABASE
flight_db TO admin_user;

-- 항공사 테이블
CREATE TABLE IF NOT EXISTS p_airlines
(
    id
    UUID
    PRIMARY
    KEY,
    airline_code
    VARCHAR
(
    10
) NOT NULL UNIQUE,
    airline_name VARCHAR
(
    100
) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR
(
    100
),
    updated_at TIMESTAMP,
    updated_by VARCHAR
(
    100
),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR
(
    100
)
    );

-- 공항 테이블
CREATE TABLE IF NOT EXISTS p_airports
(
    id
    UUID
    PRIMARY
    KEY,
    airport_name
    VARCHAR
(
    100
) NOT NULL,
    airport_code VARCHAR
(
    10
) NOT NULL UNIQUE,
    airport_city VARCHAR
(
    100
) NOT NULL,
    airport_country VARCHAR
(
    100
) NOT NULL,

    created_at TIMESTAMP,
    created_by VARCHAR
(
    100
),
    updated_at TIMESTAMP,
    updated_by VARCHAR
(
    100
),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR
(
    100
)
    );

-- 항공편 테이블
CREATE TABLE IF NOT EXISTS p_flights
(
    id
    UUID
    PRIMARY
    KEY,
    flight_num
    VARCHAR
(
    20
) NOT NULL,
    airline_id UUID NOT NULL,
    departure_airport_id UUID NOT NULL,
    arrival_airport_id UUID NOT NULL,
    departure_date TIMESTAMP NOT NULL,
    arrival_date TIMESTAMP NOT NULL,
    duration INTERVAL NOT NULL,
    price NUMERIC
(
    10,
    2
) NOT NULL,
    remaining_seats INTEGER NOT NULL,

    created_at TIMESTAMP,
    created_by VARCHAR
(
    100
),
    updated_at TIMESTAMP,
    updated_by VARCHAR
(
    100
),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR
(
    100
),
    CONSTRAINT fk_flight_airline FOREIGN KEY
(
    airline_id
) REFERENCES p_airlines
(
    id
) ON DELETE CASCADE,
    CONSTRAINT fk_departure_airport FOREIGN KEY
(
    departure_airport_id
) REFERENCES p_airports
(
    id
)
  ON DELETE CASCADE,
    CONSTRAINT fk_arrival_airport FOREIGN KEY
(
    arrival_airport_id
) REFERENCES p_airports
(
    id
)
  ON DELETE CASCADE
    );
