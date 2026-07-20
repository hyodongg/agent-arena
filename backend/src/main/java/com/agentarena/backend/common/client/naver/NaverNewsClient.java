package com.agentarena.backend.common.client.naver;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

/**
 * 네이버 검색 API(뉴스) 클라이언트.
 *
 * <p>정확도순(sort=sim)으로 받아도 종목과 무관한 기사가 섞여 나온다. 관련성 판정은 이 클래스가 아니라
 * LLM이 한다. 여기서는 응답을 쓸 수 있는 형태로 정리하는 것까지만 한다.
 */
@Slf4j
@Component
public class NaverNewsClient {

    private static final DateTimeFormatter PUB_DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;

    private final RestClient restClient;

    public NaverNewsClient(NaverProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("X-Naver-Client-Id", properties.clientId())
                .defaultHeader("X-Naver-Client-Secret", properties.clientSecret())
                .build();
    }

    public List<NaverNewsArticle> search(String query, int display) {
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", query)
                        .queryParam("display", display)
                        .queryParam("sort", "sim")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("items")) {
            return List.of();
        }

        List<NaverNewsArticle> articles = new ArrayList<>();
        for (JsonNode item : response.path("items")) {
            String title = stripMarkup(item.path("title").asString());
            String link = item.path("link").asString();
            LocalDateTime publishedAt = parsePubDate(item.path("pubDate").asString());
            if (!title.isBlank() && !link.isBlank()) {
                articles.add(new NaverNewsArticle(title, link, publishedAt));
            }
        }
        return articles;
    }

    /** 검색 결과 제목에는 {@code <b>} 강조 태그와 HTML 엔티티가 섞여 있다. */
    private String stripMarkup(String raw) {
        return raw.replaceAll("<[^>]+>", "")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .trim();
    }

    private LocalDateTime parsePubDate(String raw) {
        try {
            return ZonedDateTime.parse(raw, PUB_DATE_FORMAT).toLocalDateTime();
        } catch (Exception e) {
            log.warn("뉴스 발행시각 파싱 실패, 현재 시각으로 대체한다: {}", raw);
            return LocalDateTime.now();
        }
    }
}
