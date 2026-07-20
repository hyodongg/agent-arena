package com.agentarena.backend.domain.agent.scheduler;

import com.agentarena.backend.domain.agent.service.MemoryEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemoryEvaluationScheduler {

    private final MemoryEvaluationService memoryEvaluationService;

    @Scheduled(fixedDelay = 30000)
    public void evaluateMemories() {
        memoryEvaluationService.evaluatePending();
    }
}
