# 오늘 탄다: 최저가 여행을 원하는 사용자를 위한 특가 항공권 예매 서비스
![7조_프로젝트커버](https://github.com/user-attachments/assets/248240e6-90d3-4f68-a767-8ea168e0a860)

## 프로젝트 소개
오늘 탄다 프로젝트는 특가 및 일반 항공권을 사용자에게 제공하고, 실시간 항공권 조회 및 예약 기능을 통해 편리하고 신뢰할 수 있는 항공권 구매 경험을 제공하는 서비스입니다.
사용자는 원하는 조건의 항공권을 검색하고, 실시간으로 예약 및 결제를 진행할 수 있습니다.
### 프로젝트 목적
1. **서비스 구조 및 아키텍처**
    - MSA 기반의 유연하고 확장 가능한 서비스 아키텍처 설계 및 개발
    - 서비스 간 독립성과 분산 처리를 고려한 도메인 분리
    - 비동기 메시징을 활용한 이벤트 기반 아키텍처 적용
2. **서비스 안정성 확보**
    - 대용량 트래픽 환경에서도 안정적인 서비스 제공
    - 장애 감지 및 대응을 위한 모니터링 시스템 구축
    - 예약 중 장애나 오류 발생 시 복구 및 재처리 로직 적용
3. **성능 및 사용자 경험 최적화**
    - 실시간 특가 항공편 조회와 예약 기능의 응답 속도 개선
    - 대기열 시스템과 동시성 제어를 통한 중복 예약 방지 및 순차 처리
    - 높은 처리량을 유지하면서도 사용자 경험을 해치지 않는 설계


## 팀원 소개 및 역할
| 역할           | 담당자       | 담당                                             |
|------------------|----------------|-------------------------------------------------|
| **리더** | 진강훈       | 예약 대기열 서비스와 결제 서비스 설계 및 구현 |
| **테크-리더**  | 김승수       |      사용자의 인증, 인가 서비스 설계 및 구현, CI/CD PipeLine 구축 및 배포            |
| **서브-리더** | 서진영      |        예약 서비스 설계 및 구현 및 모니터링 시각화           |
| **서서브-리더** | 오연주       |     항공 서비스 설계 및 구현, 기획 → 설계 → 개발 전 과정을 문서화하여 팀원 간 원활한 의사소통 유도          |

## 서비스 구성 및 실행
<img width="750" alt="image (7)" src="https://github.com/user-attachments/assets/82353c1e-af2b-43e6-b50a-de94097b2303" />


### API 명세서
API 명세서 ☞ [여기로](https://www.notion.so/teamsparta/API-1cb2dc3ef5148015a607f0b2d76c6962)

## 테이블 명세서 및 ERD
테이블 명세서 ☞ [여기로](https://www.notion.so/teamsparta/1cb2dc3ef51480e098cceb210a6af62e)
![항공권 예매 서비스 (4)](https://github.com/user-attachments/assets/083d6c62-7d02-4c73-8dd6-9e89a4fc2fe1)

## 주요 기능
- 사용자 관리
    - Redis를 사용하여 토큰 관리
      → 사용자의 권한 상태 변경에 따라 토큰 블랙리스트, 만료 처리를 위한 토큰 버전 추가
    - 토큰 버전 변경시 Kafka를 통해 gateway에서 토큰 만료 비동기 처리
- 실시간 항공편 조회
    - 외부 API 연동을 통한 실시간 항공편 조회 기능 구현
    - 검색 날짜 기준 최저가 순으로 검색
    - Redis cache를 통한 항공편 조회 성능 최적화
- 대기열의 생성 및 관리
    - 좌석 선점형 대기열 → 좌석 선택하여 예약 진행 중에 다른 사용자는 같은 좌석에 대한 예약 진행 불가
    - Redisson을 통한 분산 락 적용으로 동시성 제어 → 좌석 수에 대한 데이터 정합성 보장
    - Redis SortedSet을 통한 대기열 생성으로 순차적인 처리 → 먼저 선점한 사용자의 예약 순서를 보장
- 임시 예약 및 예약 생성
    - 대기열 진입 성공시 Kafka를 통해 예약 생성 비동기 처리
    - Redis에 임시 예약 정보 생성 및 TTL 지정 → 기간내 탑승객 정보 입력 및 결제 완료 시 예약 확정
- 결제
    - 포트원 PG 대행사 연동을 통한 결제 시스템
    - 예약 생성시 결제 요청 → 결제 승인 처리, 예약 취소시 → 결제 취소 처리


## Trouble Shooting
대기열 선점 중 과도한 항공편 조회 발생 ☞[여기로](https://github.com/homeProtector/oneul-tanda/wiki/1.-대기열-선점-중-과도한-항공편-조회-발생-및-캐시를-통한-DB-부하-완화)

사용자 서비스 개발 과정에서 과도한 의존성 ☞[여기로](https://github.com/homeProtector/oneul-tanda/wiki/2.-사용자-서비스-개발과정에서-dsm을-통한-의존성-체크-후-리팩토링-진행)

MySQL 정적 데이터 삽입시 타입 불일치 오류 ☞[여기로](https://github.com/homeProtector/oneul-tanda/wiki/3.-MySQL-정적-데이터-삽입시-타입-불일치-오류)

Redis Cache 역직렬화 과정 오류 ☞[여기로](https://github.com/homeProtector/oneul-tanda/wiki/4.-Redis-Cache-역직렬화-과정-오류)

Kafka consumer 무한 재시도 이슈 ☞[여기로](https://github.com/homeProtector/oneul-tanda/wiki/5.-Kafka-컨슈머-무한-재시도-이슈)

## Technologies & Tools
### Technologies
| Java         | Spring boot         | Spring cloud         | JWT         | Spring security        |  kafka |
|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|
|<img src="https://github.com/user-attachments/assets/dc8f8162-2695-4d47-8fcd-a4d395026bdc" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/223e3dc4-ed3d-4aa1-97a1-fe5d90caae6d" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/bf6231b6-6bc2-4741-8e51-dbe928c98670" width="100" height="100">| <img src="https://github.com/user-attachments/assets/f3cae58b-77e4-4813-bb4a-40e6bfb5e26a" width="100" height="100">| <img src="https://github.com/user-attachments/assets/23012343-7981-40b4-9eda-100611a21276" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/245e0a3d-04ec-408f-8223-3803b7812029" width="100" height="100"> |

 |PostgreSQL         | MySQL         | JPA         | QueryDSL         |
 |-------------------|-------------------|-------------------|-------------------|
 |<img src="https://github.com/user-attachments/assets/97fdd97e-09a8-4a34-bcbd-a6f1aaeaf1c1" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/7fe679d8-362c-4f9f-9f64-e5ca0ef26843" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/a2c92f45-4b68-40ce-9cce-7e43e73d33f7" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/68e327b0-7eaa-44f0-be45-762c9723916b" width="100" height="100">|<img src="" width="100" height="100"> |
 


### Tools
| Redis         | Docker         | Swagger         |   Slack       | Grafana | Prometheus |
|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|
|<img src="https://github.com/user-attachments/assets/3e8f2836-045f-4d1a-85f1-0cab931032ea" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/69bfa62f-716f-4723-956d-70c8f5de15d7" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/a8dcc1e7-5534-4c2e-ae67-635ec5515cea" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/89d4adf1-a52f-4c6a-8d20-725d113d0569" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/14676353-1fb9-47f8-8c28-ebf640682870" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/d02db004-2a21-4f47-98bb-195fabe4e0fd" width="100" height="100"> |

|  Git        | GitHub         |   GitHubActions       | EC2 |
|-------------------|-------------------|-------------------|-------------------|
|<img src="https://github.com/user-attachments/assets/88d6c4c8-39b7-4147-a9b8-cc0e88535ec4" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/feb0d43f-823d-4ca1-a1dc-d0db3de63c67" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/2ddb9b62-b74a-4114-8f45-87a562ab6f06" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/52827718-a3d0-47e9-bc2d-d609d811bba2" width="100" height="100"> |


