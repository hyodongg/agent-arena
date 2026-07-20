package com.agentarena.backend.domain.agent.service;

import com.agentarena.backend.domain.agent.AgentMemory;
import com.agentarena.backend.domain.agent.repository.AgentMemoryRepository;
import com.agentarena.backend.domain.order.OrderType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 체결 직후에는 성과를 알 수 없으므로, 일정 시간이 지난 뒤 현재가와 비교해 수익률을 채운다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryEvaluationService {

    private static final int EVALUATION_DELAY_MINUTES = 10;

    private final AgentMemoryRepository agentMemoryRepository;

    @Transactional
    public void evaluatePending() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(EVALUATION_DELAY_MINUTES);
        List<AgentMemory> pending = agentMemoryRepository.findByReturnRateIsNullAndCreatedAtBefore(threshold);
        if (pending.isEmpty()) {
            return;
        }

        for (AgentMemory memory : pending) {
            memory.evaluate(calculateReturnRate(memory));
        }
        log.info("기억 평가 완료: {}건", pending.size());
    }

    /** 매수는 오르면 이득, 매도는 내리면 이득이다. */
    private double calculateReturnRate(AgentMemory memory) {
        BigDecimal executionPrice = memory.getExecutionPrice();
        BigDecimal currentPrice = memory.getStock().getCurrentPrice();

        BigDecimal delta = memory.getAction() == OrderType.BUY
                ? currentPrice.subtract(executionPrice)
                : executionPrice.subtract(currentPrice);

        return delta.divide(executionPrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
