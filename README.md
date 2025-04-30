# 오늘 탄다 - 최저가 여행을 원하는 사용자를 위한 특가 항공권 예매 서비스
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
|----------------|--------------|---------------------------------------------------|
| **리더** | 진강훈       | 예약 대기열 서비스와 결제 서비스 설계 및 구현 |
| **테크-리더**  | 김승수       |      사용자의 인증, 인가 서비스 설계 및 구현, CI/CD PipeLine 구축 및 배포            |
| **서브-리더** | 서진영      |        예약 서비스 설계 및 구현 및 모니터링 시각화           |
| **서서브-리더** | 오연주       |     항공 서비스 설계 및 구현           |

## 서비스 구성 및 실행


### 각 서비스별 endpoint
API 명세서 ☞ [여기로](https://www.notion.so/teamsparta/API-1cb2dc3ef5148015a607f0b2d76c6962)

## 테이블 명세서 및 ERD
테이블 명세서 ☞ [여기로](https://www.notion.so/teamsparta/1cb2dc3ef51480e098cceb210a6af62e)


## Trouble Shooting

### 트러블 슈팅 1
#### 문제: 

#### 해결과정
##### 고민했던 방안

##### 결과 

#### 해결방법


### 트러블 슈팅 2
#### 문제: 

#### 해결 과정
##### 고민했던 방안

##### 결과

### 트러블 슈팅 3
#### 문제: 

#### 해결 과정
##### 고민했던 방안

##### 결과

### 트러블 슈팅 4
#### 문제: 

#### 해결 과정
##### 고민했던 방안

##### 결과

## Technologies & Tools
### Technologies
| Java         | Spring boot         | Spring cloud         | JWT         | Spring security        |  kafka |
|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|
|<img src="https://github.com/user-attachments/assets/dc8f8162-2695-4d47-8fcd-a4d395026bdc" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/223e3dc4-ed3d-4aa1-97a1-fe5d90caae6d" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/bf6231b6-6bc2-4741-8e51-dbe928c98670" width="100" height="100">| <img src="https://github.com/user-attachments/assets/f3cae58b-77e4-4813-bb4a-40e6bfb5e26a" width="100" height="100">| <img src="https://github.com/user-attachments/assets/23012343-7981-40b4-9eda-100611a21276" width="100" height="100"> |<img src="" width="100" height="100"> |

 |PostgreSQL         | MySQL         | JPA         | QueryDSL         |
 |-------------------|-------------------|-------------------|-------------------|
 |<img src="https://github.com/user-attachments/assets/97fdd97e-09a8-4a34-bcbd-a6f1aaeaf1c1" width="100" height="100"> |<img src="" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/2b9b919e-e615-4b0d-9976-b459480a78ed" width="100" height="100"> | <img src="https://github.com/user-attachments/assets/6d666579-7b45-4f62-b735-1f750ede06de" width="100" height="100">|<img src="" width="100" height="100"> |
 
### Tools
| Redis         | Docker         | Swagger         |  Git        | GitHub         |   GitHubActions       |  Slack       | Grafana | EC2 |
|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|-------------------|
|<img src="https://github.com/user-attachments/assets/3e8f2836-045f-4d1a-85f1-0cab931032ea" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/69bfa62f-716f-4723-956d-70c8f5de15d7" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/a8dcc1e7-5534-4c2e-ae67-635ec5515cea" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/88d6c4c8-39b7-4147-a9b8-cc0e88535ec4" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/feb0d43f-823d-4ca1-a1dc-d0db3de63c67" width="100" height="100"> |<img src="" width="100" height="100"> |<img src="https://github.com/user-attachments/assets/89d4adf1-a52f-4c6a-8d20-725d113d0569" width="100" height="100"> |<img src="" width="100" height="100"> |<img src="" width="100" height="100"> |
### API docs

