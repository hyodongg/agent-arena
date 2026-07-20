package com.agentarena.backend.domain.news.service;

import com.agentarena.backend.common.client.naver.NaverNewsArticle;
import com.agentarena.backend.common.client.openai.OpenAiClient;
import com.agentarena.backend.domain.news.NewsSentiment;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 검색으로 긁어온 기사가 해당 종목의 주가와 관련 있는지, 호재인지 악재인지 LLM에게 판정시킨다.
 *
 * <p>네이버 뉴스 검색은 회사명이 스쳐 지나가기만 해도 걸린다. "카카오"로 검색하면 반려동물 이벤트나
 * 야구 기사가 나온다. 제목 키워드 매칭으로는 걸러낼 수 없어서 LLM을 쓴다.
 *
 * <p>기사를 한 건씩 묻지 않고 종목당 후보 전체를 한 번의 호출로 묶어 판정한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsClassifier {

    private static final String SYSTEM_PROMPT = """
            너는 한국 주식 시장의 뉴스를 분류하는 애널리스트다.
            주어진 기사 제목들이 특정 종목의 주가와 관련 있는지, 관련 있다면 호재인지 악재인지 판정하라.

            판정 기준:
            - relevant: 그 기업의 실적, 사업, 규제, 수주, 경영, 업황 등 주가에 영향을 줄 만한 내용이면 true.
              회사명만 스쳐 지나가는 기사(연예, 스포츠, 사건사고, 단순 이벤트/홍보)는 false.
            - sentiment: 주가에 긍정적이면 "POSITIVE", 부정적이면 "NEGATIVE",
              방향을 판단하기 어렵거나 중립이면 "NEUTRAL".

            반드시 아래 형태의 JSON만 출력하라.
            {"verdicts":[{"index":0,"relevant":true,"sentiment":"POSITIVE"}]}
            """;

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<NewsVerdict> classify(String stockName, List<NaverNewsArticle> articles) {
        if (articles.isEmpty()) {
            return List.of();
        }

        String userPrompt = buildUserPrompt(stockName, articles);
        String rawResponse = openAiClient.completeAsJson(SYSTEM_PROMPT, userPrompt);
        return parseVerdicts(rawResponse, articles.size());
    }

    private String buildUserPrompt(String stockName, List<NaverNewsArticle> articles) {
        StringBuilder prompt = new StringBuilder("종목: ").append(stockName).append("\n기사 제목:\n");
        for (int i = 0; i < articles.size(); i++) {
            prompt.append(i).append(". ").append(articles.get(i).title()).append('\n');
        }
        return prompt.toString();
    }

    private List<NewsVerdict> parseVerdicts(String rawResponse, int articleCount) {
        List<NewsVerdict> verdicts = new ArrayList<>();
        JsonNode root = objectMapper.readTree(rawResponse);

        for (JsonNode node : root.path("verdicts")) {
            int index = node.path("index").asInt(-1);
            if (index < 0 || index >= articleCount) {
                continue;
            }
            verdicts.add(new NewsVerdict(
                    index,
                    node.path("relevant").asBoolean(false),
                    toSentiment(node.path("sentiment").asString())
            ));
        }
        return verdicts;
    }

    /** @return 중립이거나 알 수 없는 값이면 null */
    private NewsSentiment toSentiment(String raw) {
        if ("POSITIVE".equalsIgnoreCase(raw)) {
            return NewsSentiment.POSITIVE;
        }
        if ("NEGATIVE".equalsIgnoreCase(raw)) {
            return NewsSentiment.NEGATIVE;
        }
        return null;
    }
}
