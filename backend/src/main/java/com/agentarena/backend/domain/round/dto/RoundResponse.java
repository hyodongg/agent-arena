package com.agentarena.backend.domain.round.dto;

import com.agentarena.backend.domain.round.Round;
import com.agentarena.backend.domain.round.RoundStatus;
import java.time.LocalDateTime;

public record RoundResponse(
        Long id,
        RoundStatus status,
        LocalDateTime startedAt,
        LocalDateTime settledAt,
        Long winningAgentId
) {

    public static RoundResponse from(Round round) {
        return new RoundResponse(
                round.getId(),
                round.getStatus(),
                round.getStartedAt(),
                round.getSettledAt(),
                round.getWinningAgentId()
        );
    }
}
