package com.agentarena.backend.common.client.kis;

import tools.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 한국투자증권(KIS) 모의투자 Open API 클라이언트.
 *
 * <p>토큰 발급이 1분당 1회로 제한되므로 발급받은 토큰을 메모리에 캐싱해 재사용한다.
 * 시세 조회도 초당 2~3건으로 제한되는데, 호출 간격 조절은 호출하는 쪽 책임이다.
 */
@Slf4j
@Component
public class KisClient {

    private static final String QUOTE_TR_ID = "FHKST01010100";
    private static final Duration TOKEN_REFRESH_MARGIN = Duration.ofMinutes(10);

    private final RestClient restClient;
    private final KisProperties properties;

    private String cachedToken;
    private Instant tokenExpiresAt;

    public KisClient(KisProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public KisQuote fetchQuote(String stockCode) {
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .build())
                .header("authorization", "Bearer " + resolveToken())
                .header("appkey", properties.appKey())
                .header("appsecret", properties.appSecret())
                .header("tr_id", QUOTE_TR_ID)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !"0".equals(response.path("rt_cd").asString())) {
            String message = response == null ? "빈 응답" : response.path("msg1").asString();
            throw new KisApiException("시세 조회 실패 (종목 %s): %s".formatted(stockCode, message));
        }

        JsonNode output = response.path("output");
        return new KisQuote(
                new BigDecimal(output.path("stck_prpr").asString()),
                new BigDecimal(output.path("acml_vol").asString())
        );
    }

    private synchronized String resolveToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt.minus(TOKEN_REFRESH_MARGIN))) {
            return cachedToken;
        }
        return issueToken();
    }

    private String issueToken() {
        JsonNode response = restClient.post()
                .uri("/oauth2/tokenP")
                .body(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", properties.appKey(),
                        "appsecret", properties.appSecret()
                ))
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.hasNonNull("access_token")) {
            String errorCode = response == null ? "빈 응답" : response.path("error_code").asString();
            String description = response == null ? "" : response.path("error_description").asString();
            throw new KisApiException("토큰 발급 실패: %s %s".formatted(errorCode, description));
        }

        cachedToken = response.path("access_token").asString();
        tokenExpiresAt = Instant.now().plusSeconds(response.path("expires_in").asLong());
        log.info("KIS 접근토큰 발급 완료. 만료 예정 {}", tokenExpiresAt);
        return cachedToken;
    }
}
