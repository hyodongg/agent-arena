package com.agentarena.backend.domain.betting.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BettingCreateRequest(
        @NotNull(message = "유저 ID는 필수입니다.") Long userId,
        @NotNull(message = "에이전트 ID는 필수입니다.") Long agentId,
        @NotNull(message = "배팅 금액은 필수입니다.") @Positive(message = "배팅 금액은 0보다 커야 합니다.") Long amount
) {
}
