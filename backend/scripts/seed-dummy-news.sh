#!/usr/bin/env bash
#
# 코스피 종목용 더미 뉴스 20건(호재 10 / 악재 10)을 시드한다.
#
# 실시세 연동이 동작하는지 확인하려면 에이전트가 매매를 해야 하고, 그러려면 뉴스가 필요하다.
# 이 더미 뉴스는 서브프로젝트 2(실뉴스 연동)에서 통째로 교체된다.
#
# 사용법: seed-kospi-stocks.sh 를 먼저 돌린 뒤
#   ./scripts/seed-dummy-news.sh [API_BASE_URL]
#
set -euo pipefail

API_BASE_URL="${1:-http://localhost:8081}"

# 종목코드|제목|POSITIVE|NEGATIVE
NEWS=(
  "005930|삼성전자, 차세대 메모리 양산 일정 앞당겨|POSITIVE"
  "005930|삼성전자 파운드리 수율 부진 지속|NEGATIVE"
  "000660|SK하이닉스 HBM 공급 계약 추가 체결|POSITIVE"
  "000660|SK하이닉스 감산 장기화 우려 확산|NEGATIVE"
  "373220|LG에너지솔루션 북미 수주 잔고 사상 최대|POSITIVE"
  "373220|LG에너지솔루션 배터리 리콜 이슈 재점화|NEGATIVE"
  "207940|삼성바이오로직스 신규 위탁생산 계약 체결|POSITIVE"
  "207940|삼성바이오로직스 임상 지연 소식|NEGATIVE"
  "005380|현대차 신차 판매 호조에 실적 전망 상향|POSITIVE"
  "005380|현대차 미국 관세 리스크 부각|NEGATIVE"
  "000270|기아 유럽 전기차 점유율 확대|POSITIVE"
  "000270|기아 생산라인 파업 가능성 제기|NEGATIVE"
  "035420|NAVER 커머스 거래액 성장 지속|POSITIVE"
  "035420|NAVER 광고 매출 둔화 우려|NEGATIVE"
  "035720|카카오 AI 서비스 유료화 순항|POSITIVE"
  "035720|카카오 규제 리스크 재부상|NEGATIVE"
  "105560|KB금융 분기 순이익 시장 기대 상회|POSITIVE"
  "105560|KB금융 건전성 지표 악화|NEGATIVE"
  "051910|LG화학 양극재 증설 투자 발표|POSITIVE"
  "051910|LG화학 석유화학 업황 부진 지속|NEGATIVE"
)

echo "종목 목록 조회 중..."
STOCK_MAP=$(curl -s "$API_BASE_URL/api/stocks" | python3 -c "
import json,sys
for s in json.load(sys.stdin):
    print(s['code'], s['id'])
")

if [[ -z "$STOCK_MAP" ]]; then
  echo "오류: 등록된 종목이 없다. seed-kospi-stocks.sh 를 먼저 실행한다." >&2
  exit 1
fi

seeded=0
failed=0
minute=0

for entry in "${NEWS[@]}"; do
  IFS='|' read -r code title sentiment <<< "$entry"

  stock_id=$(echo "$STOCK_MAP" | awk -v c="$code" '$1==c {print $2}')
  if [[ -z "$stock_id" ]]; then
    echo "  [건너뜀] $code - 등록되지 않은 종목"
    failed=$((failed + 1))
    continue
  fi

  # 주입 순서가 결정되도록 publishedAt 을 1분씩 벌린다 (오래된 것부터 주입된다)
  published_at=$(python3 -c "
from datetime import datetime, timedelta
print((datetime.now() - timedelta(minutes=60 - $minute)).strftime('%Y-%m-%dT%H:%M:%S'))
")
  minute=$((minute + 1))

  http_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_BASE_URL/api/news" \
    -H "Content-Type: application/json" \
    -d "{\"relatedStockId\":$stock_id,\"title\":\"$title\",\"sentiment\":\"$sentiment\",\"publishedAt\":\"$published_at\"}")

  if [[ "$http_code" == "201" ]]; then
    echo "  [등록] $code $sentiment - $title"
    seeded=$((seeded + 1))
  else
    echo "  [실패] $code - API 응답 $http_code"
    failed=$((failed + 1))
  fi
done

echo "----"
echo "뉴스 시드 완료: 성공 $seeded / 실패 $failed"
