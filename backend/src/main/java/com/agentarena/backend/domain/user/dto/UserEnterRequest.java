package com.agentarena.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserEnterRequest(
        @NotBlank(message = "가상 ID를 입력해주세요.") String username
) {
}
