package com.agentarena.backend.common.client.openai;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

/**
 * OpenAI 채팅 완성 클라이언트.
 *
 * <p>응답을 JSON으로 강제해서 호출하는 쪽이 파싱을 전제할 수 있게 한다.
 * 뉴스 호재/악재 판정과 에이전트 매매 결정이 모두 이 클라이언트를 쓴다.
 */
@Slf4j
@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final OpenAiProperties properties;

    public OpenAiClient(OpenAiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.apiKey())
                .build();
    }

    /** @return 모델이 돌려준 JSON 문자열 */
    public String completeAsJson(String systemPrompt, String userPrompt) {
        JsonNode response = restClient.post()
                .uri("/v1/chat/completions")
                .body(Map.of(
                        "model", properties.model(),
                        "messages", List.of(
                                Map.of("role", "system", "content", systemPrompt),
                                Map.of("role", "user", "content", userPrompt)
                        ),
                        "response_format", Map.of("type", "json_object"),
                        "temperature", 0.2
                ))
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("choices") || response.path("choices").isEmpty()) {
            throw new OpenAiApiException("LLM 응답이 비어있다");
        }

        String content = response.path("choices").get(0).path("message").path("content").asString();
        if (content == null || content.isBlank()) {
            throw new OpenAiApiException("LLM 응답 본문이 비어있다");
        }
        return content;
    }
}
