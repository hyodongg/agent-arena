package com.agentarena.backend.domain.agent.dto;

import com.agentarena.backend.domain.agent.Agent;
import com.agentarena.backend.domain.agent.AgentStyle;

public record AgentResponse(
        Long id,
        Long ownerId,
        String ownerUsername,
        String name,
        String investmentPrompt,
        Double cumulativeReturn,
        Long cashBalance,
        Long initialCapital,
        AgentStyle style
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
                agent.getInitialCapital(),
                agent.getStyle()
        );
    }
}
