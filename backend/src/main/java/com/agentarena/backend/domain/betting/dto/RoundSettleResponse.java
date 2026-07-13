package com.agentarena.backend.domain.betting.dto;

public record RoundSettleResponse(
        Long round,
        Long winningAgentId,
        String winningAgentName,
        int wonCount,
        int lostCount
) {
}
