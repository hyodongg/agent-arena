# 프론트엔드 백엔드 API 연동 설계

## 배경

백엔드 baseline(1단계) 도메인 API 8개(User, Agent, Stock, News, Order, Betting, Agent 포트폴리오, Betting 라운드 정산)가 모두 `main`에 병합되어 있다. 프론트엔드는 현재 `/login`(localStorage에만 저장, API 미호출)과 `/arena`(플레이스홀더)만 존재한다. 이번 작업으로 프론트를 실제 백엔드 REST API에 연동한다.

## 스코프

- 로그인 연동 (`POST /api/users/enter`)
- 아레나 대시보드: Stock/Agent/News/Order 목록 조회 + 5초 폴링
- 배팅: 생성(`POST /api/bettings`), 내 배팅 목록 조회, 라운드 정산(`POST /api/bettings/rounds/{round}/settle`)
- 백엔드 CORS 설정 추가 (별도 작은 커밋, 이 스펙의 이슈/PR 범위 밖)

스코프 밖(다음 이슈로 분리): 실시간 차트(Chart.js), Agent 생성 UI, 보유종목(AgentHolding) 상세 조회, SSE/실시간 스트리밍, 인증/권한.

## 아키텍처

### API 클라이언트
- `src/lib/api.ts`: `apiFetch(path, options)` 공통 fetch 래퍼 하나 + 도메인별 얇은 함수(`enterUser`, `getAgents`, `getStocks`, `getNews`, `getOrders`, `getBettings`, `createBetting`, `settleRound`).
- Base URL: `NEXT_PUBLIC_API_BASE_URL` env var, 미설정 시 코드 기본값 `http://localhost:8081`으로 폴백 (`.env*`가 gitignore 대상이라 예제 파일 대신 코드 기본값으로 해결).
- `src/lib/types.ts`: 백엔드 DTO와 1:1 대응하는 TS 인터페이스 (`UserResponse`, `AgentResponse`, `StockResponse`, `NewsResponse`, `OrderResponse`, `BettingResponse`, `RoundSettleResponse` 등).

### 데이터 페칭 전략
- 외부 라이브러리(SWR/React Query) 추가하지 않고 순수 `fetch` + `useEffect` + `setInterval` 5초 폴링. 프로젝트의 "가장 단순한 방식으로 시작 → 병목을 겪은 후 다음 기술 도입" 철학과 동일선상. 추후 SSE 도입 시 이 폴링 훅이 교체 대상이 된다.
- 폴링 로직은 `src/lib/usePolling.ts` 커스텀 훅 하나로 공유.

### 에러 처리
- 백엔드 에러 응답 `{code, message}`를 파싱해 폼/섹션에 인라인으로 메시지 표시. 별도 전역 토스트 시스템은 만들지 않는다.

### CORS
- 백엔드에 `WebMvcConfigurer` 기반 CORS 설정 추가, `localhost:3000` origin 허용. 프론트 이슈/PR과는 별개로 작은 백엔드 커밋 하나로 처리(기존 "trivial chore/config" 직커밋 컨벤션과 동일선상 — 기능 추가가 아니라 연동을 위한 인프라 설정이므로).

## 페이지/컴포넌트

### `/login`
- 기존 폼 UI 유지. 제출 시 `POST /api/users/enter` 호출.
- 응답(`id, username, tokenBalance`)을 localStorage에 저장 (`src/lib/auth.ts` 확장 — username 문자열 하나만 저장하던 것을 유저 객체 전체 저장으로 변경).
- 실패 시 폼 위에 에러 메시지 표시.

### `/arena`
로그인 유저 정보 상단 표시(username, tokenBalance) 후 아래 섹션 구성:

1. **Stocks**: 종목코드/이름/현재가/거래량 테이블. 5초 폴링.
2. **Agents**: 이름/소유자/누적수익률(`cumulativeReturn`)/현금잔고(`cashBalance`)/초기자본(`initialCapital`) 테이블. 각 행에 "배팅" 버튼 → 인라인 폼(회차 round 입력, 금액 amount 입력) → `POST /api/bettings`. 성공 시 유저 tokenBalance 갱신.
3. **News**: 최근 주입된 뉴스 피드(제목/관련종목/호재-악재/주입시각). 5초 폴링.
4. **Orders**: 최근 체결 내역(에이전트/종목/매수매도/수량/가격/체결시각). 5초 폴링.
5. **내 배팅**: `GET /api/bettings?userId=`로 로그인 유저의 배팅 내역(회차/에이전트/금액/상태) 표시.
6. **라운드 정산**: round 번호 입력 + "정산" 버튼 → `POST /api/bettings/rounds/{round}/settle`. 결과(승리 에이전트명, 적중/미적중 건수)를 카드로 표시하고, 내 배팅 리스트 + tokenBalance를 재조회해 갱신. 권한 개념이 없는 baseline 단계이므로 버튼은 누구나 누를 수 있다.

cumulativeReturn 재계산이나 보유종목 상세 표시는 하지 않고 백엔드가 내려주는 값을 그대로 신뢰해 표시한다.

## 검증 방법

- 백엔드(`./gradlew bootRun --args='--server.port=8081'`, 8080은 사용 금지)와 프론트(`pnpm dev`)를 동시에 띄운 상태에서 `pnpm dev` 브라우저로 로그인 → 대시보드 데이터 표시 → 배팅 생성 → 라운드 정산까지 수동으로 골든 패스 확인.
- 에러 경로도 확인: 잔액 부족 배팅, 존재하지 않는 라운드 정산 등.
