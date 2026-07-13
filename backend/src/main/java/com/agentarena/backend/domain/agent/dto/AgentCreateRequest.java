package com.agentarena.backend.domain.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgentCreateRequest(
        @NotNull(message = "소유자 ID는 필수입니다.") Long ownerId,
        @NotBlank(message = "에이전트 이름을 입력해주세요.") String name,
        @NotBlank(message = "투자 성향 프롬프트를 입력해주세요.") String investmentPrompt
) {
}
