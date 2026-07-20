package com.agentarena.backend.domain.news.dto;

import com.agentarena.backend.domain.news.NewsSentiment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record NewsCreateRequest(
        @NotNull(message = "관련 종목 ID는 필수입니다.") Long relatedStockId,
        @NotBlank(message = "뉴스 제목을 입력해주세요.") String title,
        @NotBlank(message = "원본 기사 URL은 필수입니다.") String sourceUrl,
        @NotNull(message = "호재/악재 메타데이터는 필수입니다.") NewsSentiment sentiment,
        @NotNull(message = "발생 시각은 필수입니다.") LocalDateTime publishedAt
) {
}
