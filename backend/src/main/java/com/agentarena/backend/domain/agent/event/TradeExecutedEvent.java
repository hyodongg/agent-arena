package com.agentarena.backend.domain.agent.event;

import com.agentarena.backend.domain.order.OrderType;
import java.math.BigDecimal;

/**
 * 체결이 일어났음을 알리는 이벤트. 기억 저장은 이 이벤트를 받아 트랜잭션 커밋 이후에 처리한다.
 */
public record TradeExecutedEvent(
        Long agentId,
        String agentName,
        Long stockId,
        String newsTitle,
        OrderType action,
        BigDecimal executionPrice
) {
}
