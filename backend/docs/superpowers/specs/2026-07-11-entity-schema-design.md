# 엔티티 + DB 스키마 설계

## 배경

Agent Arena 1단계(Baseline 구축)의 첫 조각. 현재 backend는 Spring Initializr로 생성한 빈 스캐폴딩 상태(JPA/Web/MySQL 의존성만 추가됨, 도메인 코드 없음)이며, 인증 폼/뉴스 스케줄러/봇 시뮬레이터 등 이후 모든 기능이 여기서 정의하는 6개 도메인 엔티티를 전제로 한다.

## 목표

- `CLAUDE.md`에 정의된 6개 핵심 도메인(User, Agent, Stock, Order, Betting, News)을 JPA 엔티티로 정의한다.
- 로컬 MySQL에 해당 테이블이 생성되는 것까지 확인한다.
- 컨트롤러/서비스/비즈니스 로직은 이번 범위에 포함하지 않는다 (다음 세션에서 CRUD API로 이어감).

## 패키지 구조

```
com.agentarena.backend
├── domain
│   ├── user/     (User, UserRepository)
│   ├── agent/    (Agent, AgentRepository)
│   ├── stock/    (Stock, StockRepository)
│   ├── order/    (Order, OrderType, OrderRepository)
│   ├── betting/  (Betting, BettingStatus, BettingRepository)
│   └── news/     (News, NewsSentiment, NewsRepository)
└── common
    └── BaseTimeEntity  (createdAt/updatedAt, @MappedSuperclass + JPA Auditing)
```

Repository는 엔티티당 `JpaRepository<T, Long>` 상속 인터페이스 하나씩만 만든다. 커스텀 쿼리 메서드는 이번 범위에 포함하지 않는다 — 다음 세션에서 CRUD API를 붙일 때 필요에 따라 추가한다.

## 연관관계 매핑 방식

모든 엔티티 간 참조는 `@ManyToOne(fetch = FetchType.LAZY)`로 JPA 객체 관계를 사용한다 (FK Long 필드만 들고 있는 방식은 채택하지 않음).

**이유:** 이 프로젝트의 목적은 "가장 투박한 동기식 JPA로 시작해서 실제 병목(N+1, 커넥션 고갈 등)을 겪은 뒤 다음 기술을 도입"하는 것이다. FK-only 방식은 JPA의 문제를 미리 회피하는 선택이라 프로젝트 철학과 어긋난다. 정석적인 JPA 객체 관계 매핑을 써야 로드맵 2단계(EXPLAIN 분석, 인덱스 적용)에서 마주칠 문제를 자연스럽게 만나게 된다.

## 공통: BaseTimeEntity

- `createdAt`, `updatedAt` — `@CreatedDate`, `@LastModifiedDate`
- `@MappedSuperclass`로 선언하고, `BackendApplication`에 `@EnableJpaAuditing` 추가
- 아래 6개 엔티티 전부 상속

## 엔티티 필드

### User
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| username | String (unique) | 가상 ID |
| tokenBalance | Long | 가상 토큰 잔액 |

### Agent
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| owner | User (`@ManyToOne`) | 생성한 유저 |
| name | String | 에이전트 이름 |
| investmentPrompt | String (`@Lob`, TEXT) | 투자 성향 프롬프트 |
| cumulativeReturn | Double | 누적 수익률 |

### Stock
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| code | String (unique) | 종목 코드 |
| name | String | 종목명 |
| currentPrice | BigDecimal | 현재가 |
| volume | Long | 거래량 |

### Order
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| agent | Agent (`@ManyToOne`) | 매매 주체 |
| stock | Stock (`@ManyToOne`) | 매매 대상 |
| type | Enum (`OrderType`: BUY, SELL) | 매수/매도 |
| quantity | Long | 수량 |
| price | BigDecimal | 체결가 |
| executedAt | LocalDateTime | 체결시간 |

### Betting
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| user | User (`@ManyToOne`) | 배팅한 유저 |
| agent | Agent (`@ManyToOne`) | 배팅 대상 에이전트 |
| round | Long | 배팅 회차 |
| amount | Long | 배팅 금액 |
| status | Enum (`BettingStatus`: IN_PROGRESS, WON, LOST) | 진행중/적중/미적중 |

### News
| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long (PK) | |
| relatedStock | Stock (`@ManyToOne`) | 관련 자산 |
| title | String | 뉴스 제목 |
| sentiment | Enum (`NewsSentiment`: POSITIVE, NEGATIVE) | 호재/악재 메타데이터 |
| publishedAt | LocalDateTime | 뉴스 발생 시각 |

## DB / application.yaml 설정

- `spring.datasource.url/username/password`는 `${DB_USERNAME}`, `${DB_PASSWORD}` 환경변수 플레이스홀더로 추가한다. 비밀번호를 git에 평문으로 남기지 않기 위함.
- `spring.jpa.hibernate.ddl-auto: update`, `spring.jpa.show-sql: true` — Flyway/Liquibase 같은 마이그레이션 도구 없이 JPA가 직접 테이블을 생성/갱신한다. 스키마 관리 문제를 실제로 겪기 전까지는 마이그레이션 도구를 도입하지 않는다 (진화형 아키텍처 철학).

## 작업 방식 (Git 워크플로)

1. GitHub 이슈 생성 — `[Feat] 엔티티 생성` (feat 템플릿)
2. 브랜치 `feat/#<issue번호>/entity`
3. 커밋 메시지 `[Feat] 엔티티 및 DB 스키마 생성` (필요시 여러 커밋으로 분리 가능)
4. PR 생성 — 제목 `[Feat] 엔티티 생성`, PR 템플릿에 `close #<issue번호>` 명시
5. main으로 merge

## 검증 방법

`./gradlew bootRun`으로 애플리케이션을 구동해 6개 테이블이 로컬 MySQL에 정상 생성되는지 로그(`show-sql`)와 MySQL 클라이언트로 직접 확인한다.

## 범위 밖 (Out of scope)

- 컨트롤러, 서비스 레이어, 비즈니스 로직
- 커스텀 리포지토리 쿼리 메서드
- 인증/로그인 폼
- News 스케줄러, 봇 매매 시뮬레이터
