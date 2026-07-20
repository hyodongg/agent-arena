#!/usr/bin/env bash
#
# 코스피 대형주 30종을 실제 시세와 함께 시드한다.
#
# KIS에서 종목별 현재가·누적거래량을 받아와 POST /api/stocks로 등록한다.
# 시세 조회가 초당 2~3건으로 제한되므로 500ms 간격으로 호출한다.
#
# 사용법: backend 디렉터리에서 앱을 띄운 뒤
#   ./scripts/seed-kospi-stocks.sh [API_BASE_URL]
#
set -euo pipefail

API_BASE_URL="${1:-http://localhost:8081}"
KIS_BASE_URL="https://openapivts.koreainvestment.com:29443"
THROTTLE_SECONDS=0.5

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "오류: $ENV_FILE 이 없다. KIS_APP_KEY / KIS_APP_SECRET 이 필요하다." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

if [[ -z "${KIS_APP_KEY:-}" || -z "${KIS_APP_SECRET:-}" ]]; then
  echo "오류: KIS_APP_KEY / KIS_APP_SECRET 이 .env 에 비어있다." >&2
  exit 1
fi

# 코드:종목명
STOCKS=(
  "005930:삼성전자"
  "000660:SK하이닉스"
  "373220:LG에너지솔루션"
  "207940:삼성바이오로직스"
  "005380:현대차"
  "000270:기아"
  "005490:POSCO홀딩스"
  "051910:LG화학"
  "006400:삼성SDI"
  "035420:NAVER"
  "035720:카카오"
  "105560:KB금융"
  "055550:신한지주"
  "086790:하나금융지주"
  "316140:우리금융지주"
  "034730:SK"
  "003550:LG"
  "015760:한국전력"
  "033780:KT&G"
  "017670:SK텔레콤"
  "009150:삼성전기"
  "012330:현대모비스"
  "011200:HMM"
  "010130:고려아연"
  "024110:기업은행"
  "032830:삼성생명"
  "018260:삼성에스디에스"
  "010950:S-Oil"
  "011070:LG이노텍"
  "004020:현대제철"
)

echo "KIS 접근토큰 발급 중..."
TOKEN=$(curl -s -X POST "$KIS_BASE_URL/oauth2/tokenP" \
  -H "Content-Type: application/json" \
  -d "{\"grant_type\":\"client_credentials\",\"appkey\":\"$KIS_APP_KEY\",\"appsecret\":\"$KIS_APP_SECRET\"}" \
  | python3 -c "import json,sys; d=json.load(sys.stdin); print(d.get('access_token',''))")

if [[ -z "$TOKEN" ]]; then
  echo "오류: 토큰 발급 실패. 1분당 1회 제한에 걸렸을 수 있으니 잠시 후 다시 시도한다." >&2
  exit 1
fi
echo "토큰 발급 완료."

seeded=0
failed=0

for entry in "${STOCKS[@]}"; do
  code="${entry%%:*}"
  name="${entry#*:}"

  quote=$(curl -s "$KIS_BASE_URL/uapi/domestic-stock/v1/quotations/inquire-price?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=$code" \
    -H "authorization: Bearer $TOKEN" \
    -H "appkey: $KIS_APP_KEY" \
    -H "appsecret: $KIS_APP_SECRET" \
    -H "tr_id: FHKST01010100")

  parsed=$(echo "$quote" | python3 -c "
import json,sys
d=json.load(sys.stdin)
if d.get('rt_cd') != '0':
    print('FAIL', d.get('msg1','')); sys.exit()
o=d.get('output') or {}
print('OK', o.get('stck_prpr','0'), o.get('acml_vol','0'))
")

  status=$(echo "$parsed" | cut -d' ' -f1)
  if [[ "$status" != "OK" ]]; then
    echo "  [실패] $code $name - $(echo "$parsed" | cut -d' ' -f2-)"
    failed=$((failed + 1))
    sleep "$THROTTLE_SECONDS"
    continue
  fi

  price=$(echo "$parsed" | cut -d' ' -f2)
  volume=$(echo "$parsed" | cut -d' ' -f3)

  http_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_BASE_URL/api/stocks" \
    -H "Content-Type: application/json" \
    -d "{\"code\":\"$code\",\"name\":\"$name\",\"currentPrice\":$price,\"volume\":$volume}")

  if [[ "$http_code" == "201" ]]; then
    echo "  [등록] $code $name - ${price}원 (거래량 $volume)"
    seeded=$((seeded + 1))
  else
    echo "  [실패] $code $name - API 응답 $http_code"
    failed=$((failed + 1))
  fi

  sleep "$THROTTLE_SECONDS"
done

echo "----"
echo "시드 완료: 성공 $seeded / 실패 $failed"
