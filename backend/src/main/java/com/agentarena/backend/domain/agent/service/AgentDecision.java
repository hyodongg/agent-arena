package com.agentarena.backend.domain.agent.service;

/**
 * 뉴스 하나에 대한 에이전트의 매매 결정.
 *
 * @param conviction 확신도 1~5. 매매 금액의 배수로 쓰인다.
 */
public record AgentDecision(Long agentId, AgentAction action, int conviction) {

    public boolean isTrade() {
        return action == AgentAction.BUY || action == AgentAction.SELL;
    }
}
