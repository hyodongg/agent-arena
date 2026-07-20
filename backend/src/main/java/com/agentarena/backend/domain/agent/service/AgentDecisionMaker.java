package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.common.client.openai.OpenAiClient;
import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.news.News;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 뉴스 하나에 대해 모든 에이전트의 매매 결정을 한 번의 LLM 호출로 받아온다.
 *
 * <p>에이전트마다 따로 호출하면 뉴스 한 건에 에이전트 수만큼 호출된다. 성향과 포트폴리오를
 * 한 프롬프트에 모아 넣고 결정을 한꺼번에 받는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentDecisionMaker {

    private static final int MIN_CONVICTION = 1;
    private static final int MAX_CONVICTION = 5;

    private static final String SYSTEM_PROMPT = """
            너는 여러 AI 투자 에이전트의 매매를 대신 판단하는 엔진이다.
            뉴스 한 건과 에이전트 목록이 주어진다. 에이전트마다 자기 투자 성향과 현재 포트폴리오에
            따라 어떻게 행동할지 결정하라.

            결정 기준:
            - action: 매수하면 "BUY", 매도하면 "SELL", 아무것도 안 하면 "HOLD".
            - 각 에이전트의 투자 성향 지침을 최우선으로 따르라. 같은 뉴스라도 성향이 다르면
              다르게 반응해야 한다. 보수적인 에이전트는 HOLD를 자주 선택할 수 있다.
            - 보유수량이 0인 종목은 매도할 수 없다. 그런 경우 SELL 대신 HOLD나 BUY를 선택하라.
            - conviction: 확신도 1~5 정수. 성향상 크게 베팅하는 에이전트일수록 높다.
              HOLD면 1로 둔다.
            - 에이전트에 "과거 유사 상황" 기록이 붙어있으면 참고하라. 비슷한 뉴스에서 손실을 봤던
              방식은 재고하고, 이익을 봤던 방식은 힘을 싣는다. 다만 투자 성향 지침이 우선이다.

            반드시 아래 형태의 JSON만 출력하라. 주어진 모든 에이전트에 대해 항목을 만들어야 한다.
            {"decisions":[{"agentId":1,"action":"BUY","conviction":3}]}
            """;

    private final OpenAiClient openAiClient;
    private final AgentMemoryService agentMemoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<AgentDecision> decide(News news, List<Agent> agents, Map<Long, BigDecimal> holdingQuantities) {
        Map<Long, List<RecalledMemory>> recalls = recallForAll(news, agents);
        String userPrompt = buildUserPrompt(news, agents, holdingQuantities, recalls);
        String rawResponse = openAiClient.completeAsJson(SYSTEM_PROMPT, userPrompt);
        return parseDecisions(rawResponse);
    }

    /**
     * 뉴스 제목을 한 번만 임베딩해서 모든 에이전트의 회상에 재사용한다.
     *
     * <p>회상 실패는 치명적이지 않다. 기억 없이도 결정은 내릴 수 있으므로 빈 결과로 넘어간다.
     */
    private Map<Long, List<RecalledMemory>> recallForAll(News news, List<Agent> agents) {
        try {
            float[] queryEmbedding = agentMemoryService.embed(news.getTitle());
            Map<Long, List<RecalledMemory>> recalls = new HashMap<>();
            for (Agent agent : agents) {
                recalls.put(agent.getId(), agentMemoryService.recall(agent.getId(), queryEmbedding));
            }
            int recalled = recalls.values().stream().mapToInt(List::size).sum();
            if (recalled > 0) {
                log.info("과거 기억 회상: {}건을 판단에 반영한다", recalled);
            }
            return recalls;
        } catch (Exception e) {
            log.warn("과거 기억 회상 실패, 기억 없이 판단한다: {}", e.getMessage());
            return Map.of();
        }
    }

    private String buildUserPrompt(News news, List<Agent> agents, Map<Long, BigDecimal> holdingQuantities,
                                   Map<Long, List<RecalledMemory>> recalls) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("[뉴스]\n")
                .append("종목: ").append(news.getRelatedStock().getName()).append('\n')
                .append("현재가: ").append(news.getRelatedStock().getCurrentPrice()).append("원\n")
                .append("판정: ").append(news.getSentiment() == com.agentarena.backend.domain.news.NewsSentiment.POSITIVE ? "호재" : "악재").append('\n')
                .append("제목: ").append(news.getTitle()).append("\n\n");

        prompt.append("[에이전트]\n");
        for (Agent agent : agents) {
            BigDecimal holding = holdingQuantities.getOrDefault(agent.getId(), BigDecimal.ZERO);
            prompt.append("- agentId=").append(agent.getId())
                    .append(", 이름=").append(agent.getName())
                    .append(", 현금=").append(agent.getCashBalance()).append("원")
                    .append(", 이 종목 보유수량=").append(holding.stripTrailingZeros().toPlainString())
                    .append("\n  투자 성향 지침: ").append(agent.getInvestmentPrompt())
                    .append('\n');
            appendRecalls(prompt, recalls.get(agent.getId()));
        }
        return prompt.toString();
    }

    private void appendRecalls(StringBuilder prompt, List<RecalledMemory> memories) {
        if (memories == null || memories.isEmpty()) {
            return;
        }
        prompt.append("  과거 유사 상황:\n");
        for (RecalledMemory memory : memories) {
            prompt.append("    - \"").append(memory.newsTitle()).append("\" 에서 ")
                    .append(memory.action() == com.agentarena.backend.domain.order.OrderType.BUY ? "매수" : "매도")
                    .append("해서 수익률 ").append(String.format("%.2f%%", memory.returnRate()))
                    .append('\n');
        }
    }

    private List<AgentDecision> parseDecisions(String rawResponse) {
        List<AgentDecision> decisions = new ArrayList<>();
        JsonNode root = objectMapper.readTree(rawResponse);

        for (JsonNode node : root.path("decisions")) {
            Long agentId = node.path("agentId").asLong(-1);
            AgentAction action = toAction(node.path("action").asString());
            if (agentId < 0 || action == null) {
                continue;
            }
            decisions.add(new AgentDecision(agentId, action, clampConviction(node.path("conviction").asInt(1))));
        }
        return decisions;
    }

    private AgentAction toAction(String raw) {
        if ("BUY".equalsIgnoreCase(raw)) {
            return AgentAction.BUY;
        }
        if ("SELL".equalsIgnoreCase(raw)) {
            return AgentAction.SELL;
        }
        if ("HOLD".equalsIgnoreCase(raw)) {
            return AgentAction.HOLD;
        }
        return null;
    }

    private int clampConviction(int raw) {
        return Math.max(MIN_CONVICTION, Math.min(MAX_CONVICTION, raw));
    }
}
