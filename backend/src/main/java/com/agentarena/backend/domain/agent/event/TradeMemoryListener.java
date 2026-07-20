package com.agentarena.backend.domain.agent.event;

import com.agentarena.backend.domain.agent.service.AgentMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 체결 트랜잭션이 커밋된 뒤에 기억을 저장한다.
 *
 * <p>매매 트랜잭션 안에서 별도 트랜잭션을 열어 저장하면, 바깥 트랜잭션이 이미 잡고 있는
 * {@code agents}/{@code stocks} 행 잠금을 안쪽에서 다시 기다리게 되어 락 대기 타임아웃이 난다.
 * 커밋 이후로 미루면 잠금이 풀린 상태라 경합이 없고, 임베딩 API가 실패해도 체결이 되돌아가지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TradeMemoryListener {

    private final AgentMemoryService agentMemoryService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTradeExecuted(TradeExecutedEvent event) {
        try {
            agentMemoryService.remember(
                    event.agentId(),
                    event.stockId(),
                    event.newsTitle(),
                    event.action(),
                    event.executionPrice()
            );
        } catch (Exception e) {
            log.warn("매매 기억 저장 실패 (에이전트 {}): {}", event.agentName(), e.getMessage());
        }
    }
}
