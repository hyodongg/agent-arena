package com.agentarena.backend.common.client.naver;

import java.time.LocalDateTime;

public record NaverNewsArticle(String title, String sourceUrl, LocalDateTime publishedAt) {
}
