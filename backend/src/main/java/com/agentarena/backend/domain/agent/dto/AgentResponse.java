package com.agentarena.backend.domain.agent.dto;

import com.agentarena.backend.domain.agent.Agent;

public record AgentResponse(
        Long id,
        Long ownerId,
        String ownerUsername,
        String name,
        String investmentPrompt,
        Double cumulativeReturn,
        Long cashBalance,
        Long initialCapital
) {

    public static AgentResponse from(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getOwner().getId(),
                agent.getOwner().getUsername(),
                agent.getName(),
                agent.getInvestmentPrompt(),
                agent.getCumulativeReturn(),
                agent.getCashBalance(),
                agent.getInitialCapital()
        );
    }
}
