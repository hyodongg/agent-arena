package com.agentarena.backend.domain.news.dto;

import com.agentarena.backend.domain.news.News;
import com.agentarena.backend.domain.news.NewsSentiment;
import java.time.LocalDateTime;

public record NewsResponse(
        Long id,
        Long relatedStockId,
        String relatedStockCode,
        String title,
        NewsSentiment sentiment,
        LocalDateTime publishedAt,
        LocalDateTime injectedAt
) {

    public static NewsResponse from(News news) {
        return new NewsResponse(
                news.getId(),
                news.getRelatedStock().getId(),
                news.getRelatedStock().getCode(),
                news.getTitle(),
                news.getSentiment(),
                news.getPublishedAt(),
                news.getInjectedAt()
        );
    }
}
