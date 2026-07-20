package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.domain.order.OrderType;

/**
 * 회상된 과거 기억 한 건.
 *
 * @param similarity 현재 뉴스와의 코사인 유사도
 * @param returnRate 그 매매의 수익률
 */
public record RecalledMemory(
        String newsTitle,
        String stockName,
        OrderType action,
        double returnRate,
        double similarity
) {
}
