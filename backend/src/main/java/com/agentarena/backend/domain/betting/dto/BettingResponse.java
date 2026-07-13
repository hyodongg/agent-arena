package com.agentarena.backend.domain.betting.dto;

import com.agentarena.backend.domain.betting.Betting;
import com.agentarena.backend.domain.betting.BettingStatus;

public record BettingResponse(
        Long id,
        Long userId,
        String username,
        Long agentId,
        String agentName,
        Long round,
        Long amount,
        BettingStatus status
) {

    public static BettingResponse from(Betting betting) {
        return new BettingResponse(
                betting.getId(),
                betting.getUser().getId(),
                betting.getUser().getUsername(),
                betting.getAgent().getId(),
                betting.getAgent().getName(),
                betting.getRound(),
                betting.getAmount(),
                betting.getStatus()
        );
    }
}
