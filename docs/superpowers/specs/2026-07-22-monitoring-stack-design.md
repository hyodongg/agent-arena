# 로컬 모니터링 스택 설계 (2026-07-22)

인프라 로드맵(`backend/CLAUDE.md`) 2단계의 준비 작업. 부하를 걸기 전에 "무엇이 터지는지" 볼 수 있게 관측 도구를 세운다.

## 목표

2단계 목표는 **DB 커넥션 고갈**과 **Slow Query**를 직접 목격하는 것이다. 이 둘이 그래프에 보이는 것이 최우선이다. 지표 없이 부하만 주면 병목을 눈으로 확인할 수 없다.

## Docker 사용에 대한 판단

`backend/CLAUDE.md`는 "Docker는 추후 배포 단계에서 필요 시 도입"이라고 적어뒀다. 그럼에도 모니터링 스택은 Docker로 올린다.

- 앱과 MySQL은 지금처럼 로컬(Homebrew)에 그대로 둔다.
- Prometheus / Grafana만 `docker-compose`로 띄운다.

이건 서비스를 컨테이너로 배포하는 것과 다른, 로컬 개발용 관측 도구다. 바이너리로 직접 설치하는 것보다 compose 파일 하나로 관리하는 게 훨씬 깔끔하고 재현 가능하다. 원칙과 충돌하지 않는다고 본다.

## 구성

```
Spring Boot (로컬 8081)
   │ Actuator + Micrometer → /actuator/prometheus 로 메트릭 노출
   ▼
Prometheus (Docker, 9090) ──15초 스크래핑──> 시계열 저장
   │
   ▼
Grafana (Docker, 3001) ──> 대시보드
```

### 1. 앱 계측

`build.gradle`에 의존성 2개:
- `org.springframework.boot:spring-boot-starter-actuator`
- `io.micrometer:micrometer-registry-prometheus`

이것만으로 자동 노출되는 메트릭:
- **HikariCP 커넥션 풀** — 활성/유휴/대기 커넥션, 획득 대기 시간. **커넥션 고갈이 여기 보인다.**
- **HTTP 서버** — 엔드포인트별 지연(p95/p99), 처리량, 상태코드별 카운트.
- **JVM** — 힙, GC 일시정지, 라이브 스레드 수. **스케줄러 스레드 기아가 여기 드러난다.**

`application.yaml`에 actuator 엔드포인트 노출 설정:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, prometheus
  metrics:
    tags:
      application: agent-arena
```

`prometheus` 엔드포인트만 연다. 로컬 개발용이므로 인증은 걸지 않는다.

### 2. Docker 스택

위치: `backend/monitoring/`

- `docker-compose.yml` — prometheus, grafana 두 컨테이너
- Grafana 포트는 **3001** (3000은 프론트가 쓴다)
- Prometheus 스크래핑 대상은 `host.docker.internal:8081` (컨테이너에서 호스트의 로컬 앱 접근)
- Grafana는 **프로비저닝 파일로 미리 설정**한다. 띄우면 바로 대시보드가 보이게:
  - `grafana/provisioning/datasources/` — Prometheus 데이터소스
  - `grafana/provisioning/dashboards/` — 대시보드 JSON 자동 로드

### 3. MySQL Slow Query

앱이 아니라 DB 쪽 설정이다. `backend/monitoring/README.md`에 안내한다.

```sql
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 0.5;
SET GLOBAL slow_query_log_file = '/opt/homebrew/var/mysql/slow.log';
```

mysqld_exporter는 **넣지 않는다.** 커넥션 고갈은 HikariCP 메트릭으로 보이고 Slow Query는 로그로 잡힌다. DB 세부 메트릭이 정말 필요해지면 그때 추가한다.

### 4. 대시보드 패널

- HikariCP: 활성 커넥션 / 대기 스레드 / 커넥션 획득 시간
- HTTP: 엔드포인트별 p95·p99 지연, 초당 요청 수, 에러율
- JVM: 힙 사용량, GC 일시정지, 라이브 스레드 수

## 이번 범위에 넣지 않는 것

- mysqld_exporter (위 참고)
- Alertmanager 알림 — 지금은 눈으로 보는 게 목적이다
- 실제 부하 발생기(k6 등) — 다음 단계다
- actuator 보안 — 로컬 전용이다

## 검증 방법

1. 앱을 띄우고 `curl localhost:8081/actuator/prometheus`에 HikariCP·JVM·HTTP 메트릭이 나오는지
2. `docker-compose up` 후 Prometheus(9090) Targets에서 앱이 **UP**인지
3. Grafana(3001)에 데이터소스가 붙고 대시보드 패널에 실제 값이 들어오는지
4. 몇 번 API를 호출해 HTTP 지연/HikariCP 활성 커넥션 그래프가 반응하는지
