package com.agentarena.backend.domain.news.service;

import com.agentarena.backend.domain.news.NewsSentiment;

/**
 * LLM이 기사 하나에 대해 내린 판정.
 *
 * @param index     요청에 넣은 기사 순번
 * @param relevant  해당 종목의 주가와 실제로 관련 있는 기사인지
 * @param sentiment 호재/악재. 중립이면 null이다.
 */
public record NewsVerdict(int index, boolean relevant, NewsSentiment sentiment) {

    public boolean isTradeable() {
        return relevant && sentiment != null;
    }
}
