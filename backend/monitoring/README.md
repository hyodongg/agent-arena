# 모니터링 스택

부하테스트 전에 병목을 눈으로 보기 위한 로컬 관측 스택이다. 앱과 MySQL은 로컬(Homebrew)에 그대로 두고, Prometheus / Grafana만 Docker로 띄운다.

## 실행

앱을 먼저 로컬에서 띄운다 (8081).

```bash
cd backend
./gradlew bootRun --args='--server.port=8081'
```

그 다음 모니터링 스택을 띄운다.

```bash
docker compose -f backend/monitoring/docker-compose.yml up -d
```

- Prometheus: http://localhost:9090 (Status → Targets 에서 `agent-arena`가 UP인지 확인)
- Grafana: http://localhost:3001 (익명 접근 허용, 또는 admin/admin) → Dashboards → **Agent Arena**

내리기:

```bash
docker compose -f backend/monitoring/docker-compose.yml down
```

## 대시보드 패널

- **HikariCP 커넥션** — active가 max(10)에 닿고 pending이 쌓이면 커넥션 고갈이다.
- **커넥션 획득 대기 시간** — 고갈 시 여기가 치솟는다.
- **HTTP 처리량 / 지연 p95·p99 / 에러율** — 엔드포인트별.
- **JVM 라이브 스레드** — 스케줄러 스레드 기아 관찰용.
- **JVM 힙**.

## MySQL Slow Query 로그

Prometheus로 수집하지 않고 로그로 잡는다. 로컬 MySQL 세션에서:

```sql
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 0.5;   -- 0.5초 이상 걸린 쿼리를 기록
SHOW VARIABLES LIKE 'slow_query_log_file';   -- 로그 위치 확인
```

부하를 준 뒤 로그 파일을 열어 Slow Query를 확인하고, `EXPLAIN`으로 실행 계획을 분석해 인덱스를 검토한다.
